package inkera.languages;

import java.util.HashMap;
import java.util.Map;

public class Languages {

	// Desteklenen dilleri enum'a alfabetik olarak ekleyelim
	public enum LanguageEnum {
		ARABIC,
		ENGLISH,
	//	LATIN,	
		JAPANESE,
		RUSSIAN,
		TURKISH
	}

	private LanguageEnum currentLanguage = LanguageEnum.ENGLISH; // Varsayılan dil

	// Her dil için metinleri tutacak Map'ler
	private Map<String, String> arabicStrings;
	private Map<String, String> englishStrings;
	private Map<String, String> japaneseStrings;
	private Map<String, String> russianStrings;
	private Map<String, String> turkishStrings;

	// Sign In için anahtar kelimeler
	public static final String KEY_SIGN_IN_LINK = "signInLink";
	public static final String KEY_SIGN_IN_TITLE = "signIn";
	public static final String KEY_SIGN_IN_BUTTON = "signInButton";
	
	// Sign Up için anahtar kelimeler
	public static final String KEY_SIGN_UP_LINK = "signUpLink";
	public static final String KEY_SIGN_UP_TITLE = "signUp";
	public static final String KEY_SIGN_UP_BUTTON = "signUpButton";
	
	// Verification
	public static final String KEY_VER_TITLE = "verificationTitle";
	public static final String KEY_VER_CODE = "verificationCode";
	
	// Forgot Password için anahtar kelimeler
	public static final String KEY_FORGOT_PASSWORD_LINK = "forgotPasswordLink";
	public static final String KEY_CHANGE_PASSWORD_VER = "verification";
	public static final String KEY_CHANGE_PASSWORD_TITLE = "changePassword";
	public static final String KEY_OLD_PASSWORD = "enterOldPassword";
	public static final String KEY_NEW_PASSWORD = "enterNewPassword";
	public static final String KEY_CHANGE_PASSWORD_BUTTON = "changePasswordButton";

	// Close butonu için anahtar kelime
	public static final String KEY_CLOSE = "close";
	
	// Next butonu için anahtar
	public static final String KEY_NEXT = "nextButton";
	
	// Placeholder içleri için anahtar kelimeler
	public static final String KEY_NAME = "name";
	public static final String KEY_SURNAME = "surname";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";
	
	// Ana menü anahtar kelimeler
	public static final String KEY_APP_TITLE = "appTitle";
	public static final String KEY_IDLE_GREETING = "idleGreeting";
	public static final String KEY_LANGUAGE_TOGGLE_BUTTON = "languageToggleButton";
	
	// GalleryPanel() için anahtar kelimeler
	public static final String KEY_GALLERY_BUTTON = "galleryButton";
	public static final String KEY_NEW_IMAGE_BUTTON = "newImageButton";
	public static final String KEY_GALLERY_EMPTY = "galleryEmptyMessage";
	
	// SettingsPanel() için anahtar kelimeler
	public static final String KEY_SETTINGS_BUTTON = "settingsButton";
	public static final String KEY_SETTINGS_PANEL_TITLE = "settingsPanelTitle";
	public static final String KEY_SETTINGS_COMING_SOON = "settingsComingSoon";
	
	// MarketplacePanel() için anahtar kelimeler
	public static final String KEY_MARKETPLACE_BUTTON = "marketplaceButton";
	public static final String KEY_MARKETPLACE_PANEL_TITLE = "marketplacePanelTitle";
	public static final String KEY_MARKETPLACE_COMING_SOON = "marketplaceComingSoon";
	
	// About diyaloğu için anahtar kelimeler
	public static final String KEY_ABOUT_BUTTON = "aboutButtonTitle";
	public static final String KEY_ABOUT_DIALOG_TITLE = "aboutDialogTitle";

	// Araç İpuçları için anahtar kelimeler
	public static final String KEY_TOOL_BRUSH = "toolBrush";
	public static final String KEY_TOOL_ERASER = "toolEraser";
	public static final String KEY_TOOL_LASSO = "toolLasso";
	public static final String KEY_TOOL_TRANSFORM = "toolTransform";
	public static final String KEY_TOOL_WAND = "toolWand";
	public static final String KEY_TOOL_BUCKET = "toolBucket";
	public static final String KEY_TOOL_TEXT = "toolText";
	public static final String KEY_TOOL_EYEDROPPER = "toolEyedropper";
	public static final String KEY_TOGGLE_COMPONETS_PANEL = "toggleComponentsPanel";


	public Languages() {
		loadArabicStrings();
		loadEnglishStrings();
		loadJapaneseStrings();
		loadRussianStrings();
		loadTurkishStrings();
	}

	private void loadArabicStrings() {
		arabicStrings = new HashMap<>();
		
		// Sign In
		arabicStrings.put(KEY_SIGN_IN_LINK, "تسجيل الدخول");
		arabicStrings.put(KEY_SIGN_IN_TITLE, "تسجيل الدخول");
		arabicStrings.put(KEY_SIGN_IN_BUTTON, "تسجيل الدخول");
		
		// Sign Up
		arabicStrings.put(KEY_SIGN_UP_LINK, "تسجيل");
		arabicStrings.put(KEY_SIGN_UP_TITLE, "تسجيل حساب");
		arabicStrings.put(KEY_SIGN_UP_BUTTON, "تسجيل حساب");
		
		// Forgot Password
		arabicStrings.put(KEY_FORGOT_PASSWORD_LINK, "هل نسيت كلمة السر");
		arabicStrings.put(KEY_CHANGE_PASSWORD_TITLE, "تغيير كلمة المرور");
		arabicStrings.put(KEY_OLD_PASSWORD, "كلمة المرور القديمة");
		arabicStrings.put(KEY_NEW_PASSWORD, "أدخل كلمة المرور الجديدة");
		arabicStrings.put(KEY_CHANGE_PASSWORD_BUTTON, "تغيير كلمة المرور");
		
		// Close
		arabicStrings.put(KEY_CLOSE, "إغلاق");

		// Placeholders
		arabicStrings.put(KEY_NAME, "الاسم");
		arabicStrings.put(KEY_SURNAME, "اللقب");
		arabicStrings.put(KEY_USERNAME, "اسم المستخدم");
		arabicStrings.put(KEY_EMAIL, "البريد الإلكتروني");
		arabicStrings.put(KEY_PASSWORD, "كلمة المرور");
		
		// Main Menu
		arabicStrings.put(KEY_APP_TITLE, "InkEra");
		arabicStrings.put(KEY_IDLE_GREETING, "مرحباً");
		arabicStrings.put(KEY_LANGUAGE_TOGGLE_BUTTON, "اللغة");
		
		// Gallery Panel
		arabicStrings.put(KEY_GALLERY_BUTTON, "المعرض");
		arabicStrings.put(KEY_NEW_IMAGE_BUTTON, "صورة جديدة");
		arabicStrings.put(KEY_GALLERY_EMPTY, "لا توجد مشاريع. انقر على 'صورة جديدة' للبدء!");
		
		// Settings Panel
		arabicStrings.put(KEY_SETTINGS_BUTTON, "إعدادات");
		arabicStrings.put(KEY_SETTINGS_PANEL_TITLE, "إعدادات");
		arabicStrings.put(KEY_SETTINGS_COMING_SOON, "الإعدادات - قريبا!");
		
		// Marketplace Panel
		arabicStrings.put(KEY_MARKETPLACE_BUTTON, "السوق");
		arabicStrings.put(KEY_MARKETPLACE_PANEL_TITLE, "السوق");
		arabicStrings.put(KEY_MARKETPLACE_COMING_SOON, "السوق - قريبًا");
		
		// About Dialog
		arabicStrings.put(KEY_ABOUT_BUTTON, "حول");
		arabicStrings.put(KEY_ABOUT_DIALOG_TITLE, "حول InkEra");

		// Tooltips
		arabicStrings.put(KEY_TOOL_BRUSH, "أداة الفرشاة");
		arabicStrings.put(KEY_TOOL_ERASER, "أداة الممحاة");
		arabicStrings.put(KEY_TOOL_LASSO, "أداة التحديد الحر");
		arabicStrings.put(KEY_TOOL_TRANSFORM, "أداة التحويل");
		arabicStrings.put(KEY_TOOL_WAND, "العصا السحرية");
		arabicStrings.put(KEY_TOOL_BUCKET, "أداة الدلو");
		arabicStrings.put(KEY_TOOL_TEXT, "أداة النص");
		arabicStrings.put(KEY_TOOL_EYEDROPPER, "أداة القطارة");
		arabicStrings.put(KEY_TOGGLE_COMPONETS_PANEL, "تبديل لوحة المكونات");
	}

	private void loadEnglishStrings() {
		englishStrings = new HashMap<>();

		// Sign In
		englishStrings.put(KEY_SIGN_IN_LINK, "Sign In");
		englishStrings.put(KEY_SIGN_IN_TITLE, "Sign In");
		englishStrings.put(KEY_SIGN_IN_BUTTON, "Sign In");
		
		// Sign Up
		englishStrings.put(KEY_SIGN_UP_LINK, "Sign Up");
		englishStrings.put(KEY_SIGN_UP_TITLE, "Sign Up");
		englishStrings.put(KEY_SIGN_UP_BUTTON, "Sign Up");
		
		// Forgot Password
		englishStrings.put(KEY_FORGOT_PASSWORD_LINK, "Forgot Password");
		englishStrings.put(KEY_CHANGE_PASSWORD_VER, "Verification");
		englishStrings.put(KEY_CHANGE_PASSWORD_TITLE, "Change Password");
		englishStrings.put(KEY_OLD_PASSWORD, "Old Password");
		englishStrings.put(KEY_NEW_PASSWORD, "New Password");
		englishStrings.put(KEY_CHANGE_PASSWORD_BUTTON, "Change");
		
		// Close
		englishStrings.put(KEY_CLOSE, "Close");
		
		// Next
		englishStrings.put(KEY_NEXT, "Next");
		
		// Placeholders
		englishStrings.put(KEY_NAME, "Name");
		englishStrings.put(KEY_SURNAME, "Surname");
		englishStrings.put(KEY_USERNAME, "Username");
		englishStrings.put(KEY_EMAIL, "E-Mail");
		englishStrings.put(KEY_PASSWORD, "Password");
		
		// Main Menu
		englishStrings.put(KEY_APP_TITLE, "InkEra");
		englishStrings.put(KEY_IDLE_GREETING, "Hello");
		englishStrings.put(KEY_LANGUAGE_TOGGLE_BUTTON, "Language");
		
		// Gallery Panel
		englishStrings.put(KEY_GALLERY_BUTTON, "Gallery");
		englishStrings.put(KEY_NEW_IMAGE_BUTTON, "New Image");
		englishStrings.put(KEY_GALLERY_EMPTY, "No projects found. Click 'New Image' to start!");
		
		// Settings Panel
		englishStrings.put(KEY_SETTINGS_BUTTON, "Settings");
		englishStrings.put(KEY_SETTINGS_PANEL_TITLE, "Settings");
		englishStrings.put(KEY_SETTINGS_COMING_SOON, "Settings - Coming Soon!");
		
		// Marketplace Panel
		englishStrings.put(KEY_MARKETPLACE_BUTTON, "Marketplace");
		englishStrings.put(KEY_MARKETPLACE_PANEL_TITLE, "Marketplace");
		englishStrings.put(KEY_MARKETPLACE_COMING_SOON, "Marketplace - Coming Soon!");
		
		// About Dialog
		englishStrings.put(KEY_ABOUT_BUTTON, "About");
		englishStrings.put(KEY_ABOUT_DIALOG_TITLE, "About InkEra");

		// Tooltips
		englishStrings.put(KEY_TOOL_BRUSH, "Brush Tool");
		englishStrings.put(KEY_TOOL_ERASER, "Eraser Tool");
		englishStrings.put(KEY_TOOL_LASSO, "Lasso Tool");
		englishStrings.put(KEY_TOOL_TRANSFORM, "Transform Tool");
		englishStrings.put(KEY_TOOL_WAND, "Magic Wand");
		englishStrings.put(KEY_TOOL_BUCKET, "Bucket Tool");
		englishStrings.put(KEY_TOOL_TEXT, "Text Tool");
		englishStrings.put(KEY_TOOL_EYEDROPPER, "Eyedropper");
		englishStrings.put(KEY_TOGGLE_COMPONETS_PANEL, "Toggle Components Panel");
	}

	private void loadJapaneseStrings() {
		japaneseStrings = new HashMap<>();
		
		// Sign In
		japaneseStrings.put(KEY_SIGN_IN_LINK, "ログイン");
		japaneseStrings.put(KEY_SIGN_IN_TITLE, "ログイン");
		japaneseStrings.put(KEY_SIGN_IN_BUTTON, "ログイン");
		
		// Sign Up
		japaneseStrings.put(KEY_SIGN_UP_LINK, "登録");
		japaneseStrings.put(KEY_SIGN_UP_TITLE, "アカウント作成");
		japaneseStrings.put(KEY_SIGN_UP_BUTTON, "登録");
		
		// Forgot Password
		japaneseStrings.put(KEY_FORGOT_PASSWORD_LINK, "パスワードをお忘れですか？");
		japaneseStrings.put(KEY_CHANGE_PASSWORD_TITLE, "パスワードの変更");
		japaneseStrings.put(KEY_OLD_PASSWORD, "古いパスワード");
		japaneseStrings.put(KEY_NEW_PASSWORD, "新しいパスワードを入力");
		japaneseStrings.put(KEY_CHANGE_PASSWORD_BUTTON, "パスワードを変更");
	
		// Close
		japaneseStrings.put(KEY_CLOSE, "閉じる");
		
		// Placeholders
		japaneseStrings.put(KEY_NAME, "名前");
		japaneseStrings.put(KEY_SURNAME, "姓");
		japaneseStrings.put(KEY_USERNAME, "ユーザー名");
		japaneseStrings.put(KEY_EMAIL, "メールアドレス");
		japaneseStrings.put(KEY_PASSWORD, "パスワード");
		
		// Main Menu
		japaneseStrings.put(KEY_APP_TITLE, "InkEra");
		japaneseStrings.put(KEY_IDLE_GREETING, "こんにちは");
		japaneseStrings.put(KEY_LANGUAGE_TOGGLE_BUTTON, "言語");
		
		// Gallery Panel
		japaneseStrings.put(KEY_GALLERY_BUTTON, "ギャラリー");
		japaneseStrings.put(KEY_NEW_IMAGE_BUTTON, "新しい画像");
		japaneseStrings.put(KEY_GALLERY_EMPTY, "「新しい画像」をクリックして開始してください。");
		
		// Settings Panel
		japaneseStrings.put(KEY_SETTINGS_BUTTON, "設定");
		japaneseStrings.put(KEY_SETTINGS_PANEL_TITLE, "設定");
		japaneseStrings.put(KEY_SETTINGS_COMING_SOON, "設定 - 近日公開！");
		
		// Marketplace Panel
		japaneseStrings.put(KEY_MARKETPLACE_BUTTON, "マーケットプレイス");
		japaneseStrings.put(KEY_MARKETPLACE_PANEL_TITLE, "マーケットプレイス");
		japaneseStrings.put(KEY_MARKETPLACE_COMING_SOON, "マーケットプレイス - 近日公開！");
		
		// About Dialog
		japaneseStrings.put(KEY_ABOUT_BUTTON, "について");
		japaneseStrings.put(KEY_ABOUT_DIALOG_TITLE, "InkEraについて");

		// Tooltips
		japaneseStrings.put(KEY_TOOL_BRUSH, "ブラシツール");
		japaneseStrings.put(KEY_TOOL_ERASER, "消しゴムツール");
		japaneseStrings.put(KEY_TOOL_LASSO, "なげなわツール");
		japaneseStrings.put(KEY_TOOL_TRANSFORM, "変形ツール");
		japaneseStrings.put(KEY_TOOL_WAND, "自動選択ツール");
		japaneseStrings.put(KEY_TOOL_BUCKET, "バケツツール");
		japaneseStrings.put(KEY_TOOL_TEXT, "テキストツール");
		japaneseStrings.put(KEY_TOOL_EYEDROPPER, "スポイトツール");
		japaneseStrings.put(KEY_TOGGLE_COMPONETS_PANEL, "コンポーネントパネルの切り替え");
	}
	
	private void loadRussianStrings() {
		russianStrings = new HashMap<>();
		
		// Sign In
		russianStrings.put(KEY_SIGN_IN_LINK, "Войти");
		russianStrings.put(KEY_SIGN_IN_TITLE, "Войти");
		russianStrings.put(KEY_SIGN_IN_BUTTON, "Войти");
		
		// Sign Up
		russianStrings.put(KEY_SIGN_UP_LINK, "Регистрация");
		russianStrings.put(KEY_SIGN_UP_TITLE, "Создать аккаунт");
		russianStrings.put(KEY_SIGN_UP_BUTTON, "Регистрация");
		
		// Forgot Password
		russianStrings.put(KEY_FORGOT_PASSWORD_LINK, "Забыли пароль?");
		russianStrings.put(KEY_CHANGE_PASSWORD_TITLE, "Сменить пароль");
		russianStrings.put(KEY_OLD_PASSWORD, "Старый пароль");
		russianStrings.put(KEY_NEW_PASSWORD, "Введите новый пароль");
		russianStrings.put(KEY_CHANGE_PASSWORD_BUTTON, "Сменить пароль");
	
		// Close
		russianStrings.put(KEY_CLOSE, "Закрыть");

		// Placeholders
		russianStrings.put(KEY_NAME, "Имя");
		russianStrings.put(KEY_SURNAME, "Фамилия");
		russianStrings.put(KEY_USERNAME, "Имя пользователя");
		russianStrings.put(KEY_EMAIL, "Электронная почта");
		russianStrings.put(KEY_PASSWORD, "Пароль");
		
		// Main Menu
		russianStrings.put(KEY_APP_TITLE, "InkEra");
		russianStrings.put(KEY_IDLE_GREETING, "Привет");
		russianStrings.put(KEY_LANGUAGE_TOGGLE_BUTTON, "Язык");
		
		// Gallery Panel
		russianStrings.put(KEY_GALLERY_BUTTON, "Галерея");
		russianStrings.put(KEY_NEW_IMAGE_BUTTON, "Новое изображение");
		russianStrings.put(KEY_GALLERY_EMPTY, "Нажмите «Новое изображение», чтобы начать!");
		
		// Settings Panel
		russianStrings.put(KEY_SETTINGS_BUTTON, "Настройки");
		russianStrings.put(KEY_SETTINGS_PANEL_TITLE, "Настройки");
		russianStrings.put(KEY_SETTINGS_COMING_SOON, "Настройки — Скоро!");
		
		// Marketplace Panel
		russianStrings.put(KEY_MARKETPLACE_BUTTON, "Рынок");
		russianStrings.put(KEY_MARKETPLACE_PANEL_TITLE, "Рынок");
		russianStrings.put(KEY_MARKETPLACE_COMING_SOON, "Рынок - Скоро!");
		
		// About Dialog
		russianStrings.put(KEY_ABOUT_BUTTON, "О программе");
		russianStrings.put(KEY_ABOUT_DIALOG_TITLE, "О программе InkEra");

		// Tooltips
		russianStrings.put(KEY_TOOL_BRUSH, "Инструмент 'Кисть'");
		russianStrings.put(KEY_TOOL_ERASER, "Инструмент 'Ластик'");
		russianStrings.put(KEY_TOOL_LASSO, "Инструмент 'Лассо'");
		russianStrings.put(KEY_TOOL_TRANSFORM, "Инструмент 'Трансформация'");
		russianStrings.put(KEY_TOOL_WAND, "Волшебная палочка");
		russianStrings.put(KEY_TOOL_BUCKET, "Заливка");
		russianStrings.put(KEY_TOOL_TEXT, "Инструмент 'Текст'");
		russianStrings.put(KEY_TOOL_EYEDROPPER, "Пипетка");
		russianStrings.put(KEY_TOGGLE_COMPONETS_PANEL, "Переключить панель компонентов");
	}
	
	private void loadTurkishStrings() {
		turkishStrings = new HashMap<>();

		// Sign In
		turkishStrings.put(KEY_SIGN_IN_LINK, "Giriş Yap");
		turkishStrings.put(KEY_SIGN_IN_TITLE, "Giriş Yap");
		turkishStrings.put(KEY_SIGN_IN_BUTTON, "Giriş Yap");

		// Sign Up
		turkishStrings.put(KEY_SIGN_UP_LINK, "Hesap Oluştur");
		turkishStrings.put(KEY_SIGN_UP_TITLE, "Hesap Oluştur");
		turkishStrings.put(KEY_SIGN_UP_BUTTON, "Hesap Oluştur");
		
		// Forgot Password
		turkishStrings.put(KEY_FORGOT_PASSWORD_LINK, "Şifremi Unuttum");
		turkishStrings.put(KEY_CHANGE_PASSWORD_TITLE, "Şifreyi Değiştir");
		turkishStrings.put(KEY_OLD_PASSWORD, "Eski Şifre");
		turkishStrings.put(KEY_NEW_PASSWORD, "Yeni Şifre");
		turkishStrings.put(KEY_CHANGE_PASSWORD_BUTTON, "Şifreyi Değiştir");

		// Close
		turkishStrings.put(KEY_CLOSE, "Kapat");
		
		// Placeholders
		turkishStrings.put(KEY_NAME, "Ad");
		turkishStrings.put(KEY_SURNAME, "Soyad");
		turkishStrings.put(KEY_USERNAME, "Kullanıcı Adı");
		turkishStrings.put(KEY_EMAIL, "E-Posta");
		turkishStrings.put(KEY_PASSWORD, "Şifre");
		
		// Main Menu
		turkishStrings.put(KEY_APP_TITLE, "InkEra");
		turkishStrings.put(KEY_IDLE_GREETING, "Merhaba");
		turkishStrings.put(KEY_LANGUAGE_TOGGLE_BUTTON, "Dil");
		
		// Gallery Panel
		turkishStrings.put(KEY_GALLERY_BUTTON, "Atölye");
		turkishStrings.put(KEY_NEW_IMAGE_BUTTON, "Yeni Resim");
		turkishStrings.put(KEY_GALLERY_EMPTY, "Başlamak için \"Yeni Resim\" butonuna tıklayın!");
		
		// Settings Panel
		turkishStrings.put(KEY_SETTINGS_BUTTON, "Ayarlar");
		turkishStrings.put(KEY_SETTINGS_PANEL_TITLE, "Ayarlar");
		turkishStrings.put(KEY_SETTINGS_COMING_SOON, "Ayarlar - Çok Yakında!");
		
		// Marketplace Panel
		turkishStrings.put(KEY_MARKETPLACE_BUTTON, "Market");
		turkishStrings.put(KEY_MARKETPLACE_PANEL_TITLE, "Market");
		turkishStrings.put(KEY_MARKETPLACE_COMING_SOON, "Market - Çok Yakında!");
		
		// About Dialog
		turkishStrings.put(KEY_ABOUT_BUTTON, "Hakkında");
		turkishStrings.put(KEY_ABOUT_DIALOG_TITLE, "InkEra Hakkında");

		// Tooltips
		turkishStrings.put(KEY_TOOL_BRUSH, "Fırça Aracı");
		turkishStrings.put(KEY_TOOL_ERASER, "Silgi Aracı");
		turkishStrings.put(KEY_TOOL_LASSO, "Kement Aracı");
		turkishStrings.put(KEY_TOOL_TRANSFORM, "Dönüştür Aracı");
		turkishStrings.put(KEY_TOOL_WAND, "Sihirli Değnek");
		turkishStrings.put(KEY_TOOL_BUCKET, "Boya Kovası");
		turkishStrings.put(KEY_TOOL_TEXT, "Metin Aracı");
		turkishStrings.put(KEY_TOOL_EYEDROPPER, "Damlalık");
		turkishStrings.put(KEY_TOGGLE_COMPONETS_PANEL, "Bileşenler Panelini Göster/Gizle");
	}

	public void setLanguage(LanguageEnum language) {
		this.currentLanguage = language;
	}

	public LanguageEnum getCurrentLanguage() {
		return currentLanguage;
	}

	public String getString(String key) {
		Map<String, String> currentMap = englishStrings; // Varsayılan olarak İngilizce
		switch (currentLanguage) {
			case ARABIC:
				currentMap = arabicStrings;
				break;
			case ENGLISH:
				currentMap = englishStrings;
				break;
			case JAPANESE:
				currentMap = japaneseStrings;
				break;
			case RUSSIAN:
				currentMap = russianStrings;
				break;
			case TURKISH:
				currentMap = turkishStrings;
				break;
		}

		String text = currentMap.get(key);

		// Eğer çeviri bulunamazsa, İngilizce'ye geri dön (fallback)
		if (text == null) {
		    text = englishStrings.getOrDefault(key, "!" + key + "!");
		}

		return text;
	}
}
