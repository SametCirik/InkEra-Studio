package inkera.ui.account;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Kullanıcı hesaplarını yöneten merkezi sınıf.
 * Oturum yönetimi, doğrulama ve parola sıfırlama işlevlerini içerir.
 */
public class AccountManager {

    private static AccountManager instance;
    private final List<User> users;
    private final File userDataFile;
    private User currentUser = null;

    private static final String STATUS_VERIFIED = "VERIFIED";
    private static final String STATUS_UNVERIFIED = "UNVERIFIED";

    /**
     * Kullanıcı verilerini temsil eden sınıf.
     * DÜZELTME: Bu sınıf artık "immutable" (değiştirilemez). Bir kullanıcı
     * bilgisi güncellendiğinde, eski nesne değiştirilmek yerine yeni bir
     * nesne oluşturulur. Bu, kodun daha güvenli ve tahmin edilebilir olmasını sağlar.
     */
    private static class User {
        private final String name;
        private final String surname;
        private final String username;
        private final String email;
        private final String passwordHash; // Artık final
        private final String salt;
        private final String status;         // Artık final
        private final String verificationCode; // Artık final

        public User(String name, String surname, String username, String email, String passwordHash, String salt, String status, String verificationCode) {
            this.name = name;
            this.surname = surname;
            this.username = username;
            this.email = email;
            this.passwordHash = passwordHash;
            this.salt = salt;
            this.status = status;
            this.verificationCode = verificationCode;
        }

        // Dosya formatı, null doğrulama kodunu doğru işleyecek şekilde güncellendi.
        public String toFileString() {
            return name + ";" + surname + ";" + username + ";" + email + ";" + passwordHash + ";" + salt + ";" + status + ";" + (verificationCode != null ? verificationCode : "");
        }

        public static User fromFileString(String line) {
            String[] parts = line.split(";", -1); // Sondaki boş alanları korumak için -1
            if (parts.length == 8) {
                // Boş bir verificationCode, null olarak yorumlanır.
                return new User(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7].isEmpty() ? null : parts[7]);
            }
            return null;
        }
    }

    private AccountManager() {
        String userHome = System.getProperty("user.home");
        File appDirectory = new File(userHome, ".inkera");
        if (!appDirectory.exists()) {
            appDirectory.mkdirs();
        }
        this.userDataFile = new File(appDirectory, "accounts.txt");
        this.users = new ArrayList<>();
        loadUsers();
    }

    public static synchronized AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

    public boolean signUp(String username, String email, String password, String name, String surname) {
        if (users.stream().anyMatch(u -> u.username.equalsIgnoreCase(username) || u.email.equalsIgnoreCase(email))) {
            return false;
        }
        byte[] salt = generateSalt();
        String passwordHash = hashPassword(password, salt);
        String verificationCode = generateVerificationCode();
        User newUser = new User(name, surname, username, email, passwordHash, Base64.getEncoder().encodeToString(salt), STATUS_UNVERIFIED, verificationCode);
        users.add(newUser);
        saveUsers();
        System.out.println("Doğrulama Kodu (" + email + " için): " + verificationCode);
        return true;
    }

    public boolean signIn(String usernameOrEmail, String password) {
        Optional<User> userOptional = users.stream()
            .filter(u -> u.username.equalsIgnoreCase(usernameOrEmail) || u.email.equalsIgnoreCase(usernameOrEmail))
            .findFirst();

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.status.equals(STATUS_VERIFIED)) {
                return false;
            }
            byte[] salt = Base64.getDecoder().decode(user.salt);
            String enteredPasswordHash = hashPassword(password, salt);
            
            if (enteredPasswordHash.equals(user.passwordHash)) {
                this.currentUser = user;
                return true;
            }
        }
        return false;
    }
    
    public void signOut() { // DÜZELTME: logOut -> signOut (Java standardı)
        this.currentUser = null;
    }

    public boolean isUserLoggedIn() {
        return this.currentUser != null;
    }

    public String getCurrentUserName() {
        if (isUserLoggedIn()) {
            return this.currentUser.name;
        }
        return null;
    }

    public boolean verifyAccount(String email, String code) {
        Optional<User> userOptional = users.stream()
            .filter(u -> u.email.equalsIgnoreCase(email) && u.status.equals(STATUS_UNVERIFIED))
            .findFirst();

        if (userOptional.isPresent()) {
            User userToVerify = userOptional.get();
            if (userToVerify.verificationCode != null && userToVerify.verificationCode.equals(code)) {
                // DÜZELTME: Eski kullanıcıyı silip, durumu güncellenmiş yeni bir tane ekliyoruz.
                User verifiedUser = new User(userToVerify.name, userToVerify.surname, userToVerify.username, userToVerify.email, userToVerify.passwordHash, userToVerify.salt, STATUS_VERIFIED, null);
                users.remove(userToVerify);
                users.add(verifiedUser);
                saveUsers();
                return true;
            }
        }
        return false;
    }

    public boolean requestPasswordReset(String email) {
        Optional<User> userOptional = users.stream()
            .filter(u -> u.email.equalsIgnoreCase(email) && u.status.equals(STATUS_VERIFIED))
            .findFirst();

        if (userOptional.isPresent()) {
            User userToReset = userOptional.get();
            String newCode = generateVerificationCode();
            // DÜZELTME: Kullanıcıyı yeni kodla güncelliyoruz.
            User updatedUser = new User(userToReset.name, userToReset.surname, userToReset.username, userToReset.email, userToReset.passwordHash, userToReset.salt, userToReset.status, newCode);
            users.remove(userToReset);
            users.add(updatedUser);
            saveUsers();
            System.out.println("Şifre Sıfırlama Kodu (" + email + " için): " + newCode);
            return true;
        }
        return false;
    }

    public boolean resetPassword(String email, String code, String newPassword) {
        Optional<User> userOptional = users.stream()
            .filter(u -> u.email.equalsIgnoreCase(email))
            .findFirst();
        
        if (userOptional.isPresent()) {
            User userToReset = userOptional.get();
            if (userToReset.verificationCode != null && userToReset.verificationCode.equals(code)) {
                byte[] salt = Base64.getDecoder().decode(userToReset.salt);
                String newPasswordHash = hashPassword(newPassword, salt);
                // DÜZELTME: Kullanıcıyı yeni şifresiyle güncelliyoruz.
                User updatedUser = new User(userToReset.name, userToReset.surname, userToReset.username, userToReset.email, newPasswordHash, userToReset.salt, STATUS_VERIFIED, null);
                users.remove(userToReset);
                users.add(updatedUser);
                saveUsers();
                return true;
            }
        }
        return false;
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
    
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private String hashPassword(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Parola hash'leme sırasında kritik bir hata oluştu.", e);
        }
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userDataFile))) {
            for (User user : users) {
                writer.write(user.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        if (!userDataFile.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(userDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromFileString(line);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
