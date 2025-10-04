module InkEra {
	// Tüm paketleri dışa aç
	exports inkera.ui.panels.mainmenu;
	exports inkera.ui.hyperlinks;
	exports inkera.ui.titlebars;
	exports inkera.ui.account;
	exports inkera.ui.dialogs;
	exports inkera.main;
	exports inkera.splashscreen;
	exports inkera.languages;
	exports inkera.projects;
	
	// UI paketleri arasında erişim izni
	opens inkera.ui.panels.mainmenu;
	opens inkera.ui.account;
	opens inkera.ui.dialogs;
	opens inkera.languages;
	opens inkera.main;
	opens inkera.projects;
	
	// Gerekli modül
	requires java.desktop;

	// requires transitive java.desktop;
}