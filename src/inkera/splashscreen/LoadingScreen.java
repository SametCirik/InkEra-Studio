package inkera.splashscreen;

import javax.swing.SwingUtilities;

public class LoadingScreen 
{
	public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(() -> 
		{
			SplashScreenKit.SplashScreenWithProgress splash = new SplashScreenKit.SplashScreenWithProgress();
			splash.showSplash();
			new SplashScreenKit.LoaderTask(splash).execute(); 
		});
	}
}	