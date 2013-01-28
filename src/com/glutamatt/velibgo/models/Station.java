package com.glutamatt.velibgo.models;

public class Station {
	
	@Override
	public String toString() {
		return getNom();
	}
	
	private int id;
	private String adresse;
	private String nom;
	private double latitude;
	private double longitude;
	
	private int velosDispo;
	private int placesDispo;
	public int getPlacesDispo() {
		return placesDispo;
	}
	public void setPlacesDispo(int placesDispo) {
		this.placesDispo = placesDispo;
	}
	public int getPlacesLocked() {
		return placesLocked;
	}
	public void setPlacesLocked(int placesLocked) {
		this.placesLocked = placesLocked;
	}
	public int getPlacesTotal() {
		return placesTotal;
	}
	public void setPlacesTotal(int placesTotal) {
		this.placesTotal = placesTotal;
	}
	private int placesLocked;
	private int placesTotal;
	
	

	public void setId(int int1) {
		id = int1;
	}
	public void setAdresse(String string) {
		adresse = string;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public int getId() {
		return id;
	}
	public String getAdresse() {
		return adresse;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public int getVelosDispo() {
		return velosDispo;
	}
	public void setVelosDispo(int velosDispo) {
		this.velosDispo = velosDispo;
	}

}
