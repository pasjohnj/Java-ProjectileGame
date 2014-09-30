

class DataToUserInterface { //a clean wrapper to send data from the game logic to the interface to update game status
	public String scoreBanner, generalBanner, specificBanner;
	
	DataToUserInterface(String scoreBanner, String generalBanner, String specificBanner){
		this.scoreBanner = scoreBanner;
		this.generalBanner = generalBanner;
		this.specificBanner = specificBanner;
	}

}
