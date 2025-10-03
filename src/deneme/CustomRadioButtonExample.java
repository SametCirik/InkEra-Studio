package deneme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Önceki kodunuzla aynı, sadece JPanel -> BracketPanel değişikliği var
public class CustomRadioButtonExample extends JFrame {

    public CustomRadioButtonExample() {
        setTitle("Özel Radio Button Örneği");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Arka plan rengini ayarlayarak daha hoş bir görünüm elde edelim
        getContentPane().setBackground(new Color(240, 240, 240)); 
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

        JLabel secimEtiketi = new JLabel("Henüz seçim yapmadınız.");
        secimEtiketi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        secimEtiketi.setForeground(Color.DARK_GRAY);

        // ... CustomRadioButton oluşturma kodları aynı kalıyor ...
        CustomRadioButton option1 = new CustomRadioButton("Seçenek A");
        // ... (option2, option3, option4 ve renk ayarları aynı)
        CustomRadioButton option2 = new CustomRadioButton("Seçenek B");
        CustomRadioButton option3 = new CustomRadioButton("Seçenek C");
        CustomRadioButton option4 = new CustomRadioButton("Seçenek D");
        CustomRadioButton option5 = new CustomRadioButton("Varsayılan Renk");

        option1.setSelectedColor(new Color(100, 200, 100));
        option1.setBorderColor(Color.GRAY);
        option2.setSelectedColor(new Color(100, 200, 100));
        option2.setBorderColor(Color.GRAY);
        option3.setSelectedColor(new Color(100, 200, 100));
        option3.setBorderColor(Color.GRAY);
        option4.setSelectedColor(new Color(100, 200, 100));
        option4.setBorderColor(Color.GRAY);
        option5.setSelectedColor(new Color(100, 200, 100));
        option5.setBorderColor(Color.GRAY);
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(option1);
        buttonGroup.add(option2);
        buttonGroup.add(option3);
        buttonGroup.add(option4);

        ActionListener listener = e -> {
            CustomRadioButton selectedButton = (CustomRadioButton) e.getSource();
            secimEtiketi.setText("Seçiminiz: " + selectedButton.getText());
        };

        option1.addActionListener(listener);
        option2.addActionListener(listener);
        option3.addActionListener(listener);
        option4.addActionListener(listener);
        option5.addActionListener(listener);

        // --- DEĞİŞİKLİK BURADA ---
        // JPanel yerine BracketPanel kullanıyoruz
        BracketPanel buttonPanel = new BracketPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false); // Panelin arka planını şeffaf yap ki JFrame'in rengi görünsün

        buttonPanel.add(option1);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Boşlukları biraz azaltalım
        buttonPanel.add(option2);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonPanel.add(option3);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonPanel.add(option4);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        buttonPanel.add(option5);
        
        // Pencereye bileşenleri ekle
        add(secimEtiketi);
        add(buttonPanel);

//      option4.setSelected(true);
//      secimEtiketi.setText("Seçiminiz: " + option4.getText());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomRadioButtonExample().setVisible(true);
        });
    }
}
