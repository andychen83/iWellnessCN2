package com.lefu.es.entity;

import java.io.Serializable;
/**
 * 营养成分表
 * @author Leon
 * 2015-11-17
 */
public class NutrientBo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String nutrientNo;
	private String nutrientDesc;
	
	private String nutrientWater;
	private String nutrientEnerg;
	private String nutrientProtein;
	private String nutrientLipidTot;
	private String nutrientAsh;
	private String nutrientCarbohydrt;
	private String nutrientFiberTD;
	private String nutrientSugarTot;
	private String nutrientCalcium;
	private String nutrientIron;
	private String nutrientMagnesium;
	private String nutrientPhosphorus;
	private String nutrientPotassium;
	private String nutrientSodium;
	private String nutrientZinc;
	private String nutrientCopper;
	private String nutrientManganese;
	private String nutrientSelenium;
	private String nutrientVitc;
	private String nutrientThiamin;
	private String nutrientRiboflavin;
	private String nutrientNiacin;
	private String nutrientPantoAcid;
	private String nutrientVitB6;
	private String nutrientFolateTot;
	private String nutrientFolicAcid;
	private String nutrientFoodFolate;
	private String nutrientFolateDfe;
	private String nutrientCholineTot;
	private String nutrientVitB12;
	private String nutrientVitAiu;
	private String nutrientVitArae;
	private String nutrientRetinol;
	private String nutrientAlphaCarot;
	private String nutrientBetaCarot;
	private String nutrientBetaCrypt;
	private String nutrientLycopene;
	private String nutrientLutZea;
	private String nutrientVite;
	private String nutrientVitd;
	private String nutrientVitDiu;
	private String nutrientVitK;
	private String nutrientFaSat;
	private String nutrientFaMono;
	private String nutrientFaPoly;
	private String nutrientCholestrl;
	private String nutrientGmWt1;
	private String nutrientGmWtDesc1;
	private String nutrientGmWt2;
	private String nutrientGmWtDesc2;
	private String nutrientRefusePct;
	
	private float  foodWeight;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNutrientNo() {
		return nutrientNo;
	}
	public void setNutrientNo(String nutrientNo) {
		this.nutrientNo = nutrientNo;
	}
	public String getNutrientDesc() {
		return nutrientDesc;
	}
	public void setNutrientDesc(String nutrientDesc) {
		this.nutrientDesc = nutrientDesc;
	}
	public String getNutrientWater() {
		return nutrientWater;
	}
	public void setNutrientWater(String nutrientWater) {
		this.nutrientWater = nutrientWater;
	}
	public String getNutrientEnerg() {
		return nutrientEnerg;
	}
	public void setNutrientEnerg(String nutrientEnerg) {
		this.nutrientEnerg = nutrientEnerg;
	}
	public String getNutrientProtein() {
		return nutrientProtein;
	}
	public void setNutrientProtein(String nutrientProtein) {
		this.nutrientProtein = nutrientProtein;
	}
	public String getNutrientLipidTot() {
		return nutrientLipidTot;
	}
	public void setNutrientLipidTot(String nutrientLipidTot) {
		this.nutrientLipidTot = nutrientLipidTot;
	}
	public String getNutrientAsh() {
		return nutrientAsh;
	}
	public void setNutrientAsh(String nutrientAsh) {
		this.nutrientAsh = nutrientAsh;
	}
	public String getNutrientCarbohydrt() {
		return nutrientCarbohydrt;
	}
	public void setNutrientCarbohydrt(String nutrientCarbohydrt) {
		this.nutrientCarbohydrt = nutrientCarbohydrt;
	}
	public String getNutrientFiberTD() {
		return nutrientFiberTD;
	}
	public void setNutrientFiberTD(String nutrientFiberTD) {
		this.nutrientFiberTD = nutrientFiberTD;
	}
	public String getNutrientSugarTot() {
		return nutrientSugarTot;
	}
	public void setNutrientSugarTot(String nutrientSugarTot) {
		this.nutrientSugarTot = nutrientSugarTot;
	}
	public String getNutrientCalcium() {
		return nutrientCalcium;
	}
	public void setNutrientCalcium(String nutrientCalcium) {
		this.nutrientCalcium = nutrientCalcium;
	}
	public String getNutrientIron() {
		return nutrientIron;
	}
	public void setNutrientIron(String nutrientIron) {
		this.nutrientIron = nutrientIron;
	}
	public String getNutrientMagnesium() {
		return nutrientMagnesium;
	}
	public void setNutrientMagnesium(String nutrientMagnesium) {
		this.nutrientMagnesium = nutrientMagnesium;
	}
	public String getNutrientPhosphorus() {
		return nutrientPhosphorus;
	}
	public void setNutrientPhosphorus(String nutrientPhosphorus) {
		this.nutrientPhosphorus = nutrientPhosphorus;
	}
	public String getNutrientPotassium() {
		return nutrientPotassium;
	}
	public void setNutrientPotassium(String nutrientPotassium) {
		this.nutrientPotassium = nutrientPotassium;
	}
	public String getNutrientSodium() {
		return nutrientSodium;
	}
	public void setNutrientSodium(String nutrientSodium) {
		this.nutrientSodium = nutrientSodium;
	}
	public String getNutrientZinc() {
		return nutrientZinc;
	}
	public void setNutrientZinc(String nutrientZinc) {
		this.nutrientZinc = nutrientZinc;
	}
	public String getNutrientCopper() {
		return nutrientCopper;
	}
	public void setNutrientCopper(String nutrientCopper) {
		this.nutrientCopper = nutrientCopper;
	}
	public String getNutrientManganese() {
		return nutrientManganese;
	}
	public void setNutrientManganese(String nutrientManganese) {
		this.nutrientManganese = nutrientManganese;
	}
	public String getNutrientSelenium() {
		return nutrientSelenium;
	}
	public void setNutrientSelenium(String nutrientSelenium) {
		this.nutrientSelenium = nutrientSelenium;
	}
	public String getNutrientVitc() {
		return nutrientVitc;
	}
	public void setNutrientVitc(String nutrientVitc) {
		this.nutrientVitc = nutrientVitc;
	}
	public String getNutrientThiamin() {
		return nutrientThiamin;
	}
	public void setNutrientThiamin(String nutrientThiamin) {
		this.nutrientThiamin = nutrientThiamin;
	}
	public String getNutrientRiboflavin() {
		return nutrientRiboflavin;
	}
	public void setNutrientRiboflavin(String nutrientRiboflavin) {
		this.nutrientRiboflavin = nutrientRiboflavin;
	}
	public String getNutrientNiacin() {
		return nutrientNiacin;
	}
	public void setNutrientNiacin(String nutrientNiacin) {
		this.nutrientNiacin = nutrientNiacin;
	}
	public String getNutrientPantoAcid() {
		return nutrientPantoAcid;
	}
	public void setNutrientPantoAcid(String nutrientPantoAcid) {
		this.nutrientPantoAcid = nutrientPantoAcid;
	}
	public String getNutrientVitB6() {
		return nutrientVitB6;
	}
	public void setNutrientVitB6(String nutrientVitB6) {
		this.nutrientVitB6 = nutrientVitB6;
	}
	public String getNutrientFolateTot() {
		return nutrientFolateTot;
	}
	public void setNutrientFolateTot(String nutrientFolateTot) {
		this.nutrientFolateTot = nutrientFolateTot;
	}
	public String getNutrientFolicAcid() {
		return nutrientFolicAcid;
	}
	public void setNutrientFolicAcid(String nutrientFolicAcid) {
		this.nutrientFolicAcid = nutrientFolicAcid;
	}
	public String getNutrientFoodFolate() {
		return nutrientFoodFolate;
	}
	public void setNutrientFoodFolate(String nutrientFoodFolate) {
		this.nutrientFoodFolate = nutrientFoodFolate;
	}
	public String getNutrientFolateDfe() {
		return nutrientFolateDfe;
	}
	public void setNutrientFolateDfe(String nutrientFolateDfe) {
		this.nutrientFolateDfe = nutrientFolateDfe;
	}
	public String getNutrientCholineTot() {
		return nutrientCholineTot;
	}
	public void setNutrientCholineTot(String nutrientCholineTot) {
		this.nutrientCholineTot = nutrientCholineTot;
	}
	public String getNutrientVitB12() {
		return nutrientVitB12;
	}
	public void setNutrientVitB12(String nutrientVitB12) {
		this.nutrientVitB12 = nutrientVitB12;
	}
	public String getNutrientVitAiu() {
		return nutrientVitAiu;
	}
	public void setNutrientVitAiu(String nutrientVitAiu) {
		this.nutrientVitAiu = nutrientVitAiu;
	}
	public String getNutrientVitArae() {
		return nutrientVitArae;
	}
	public void setNutrientVitArae(String nutrientVitArae) {
		this.nutrientVitArae = nutrientVitArae;
	}
	public String getNutrientRetinol() {
		return nutrientRetinol;
	}
	public void setNutrientRetinol(String nutrientRetinol) {
		this.nutrientRetinol = nutrientRetinol;
	}
	public String getNutrientAlphaCarot() {
		return nutrientAlphaCarot;
	}
	public void setNutrientAlphaCarot(String nutrientAlphaCarot) {
		this.nutrientAlphaCarot = nutrientAlphaCarot;
	}
	public String getNutrientBetaCarot() {
		return nutrientBetaCarot;
	}
	public void setNutrientBetaCarot(String nutrientBetaCarot) {
		this.nutrientBetaCarot = nutrientBetaCarot;
	}
	public String getNutrientBetaCrypt() {
		return nutrientBetaCrypt;
	}
	public void setNutrientBetaCrypt(String nutrientBetaCrypt) {
		this.nutrientBetaCrypt = nutrientBetaCrypt;
	}
	public String getNutrientLycopene() {
		return nutrientLycopene;
	}
	public void setNutrientLycopene(String nutrientLycopene) {
		this.nutrientLycopene = nutrientLycopene;
	}
	public String getNutrientLutZea() {
		return nutrientLutZea;
	}
	public void setNutrientLutZea(String nutrientLutZea) {
		this.nutrientLutZea = nutrientLutZea;
	}
	public String getNutrientVite() {
		return nutrientVite;
	}
	public void setNutrientVite(String nutrientVite) {
		this.nutrientVite = nutrientVite;
	}
	public String getNutrientVitd() {
		return nutrientVitd;
	}
	public void setNutrientVitd(String nutrientVitd) {
		this.nutrientVitd = nutrientVitd;
	}
	public String getNutrientVitDiu() {
		return nutrientVitDiu;
	}
	public void setNutrientVitDiu(String nutrientVitDiu) {
		this.nutrientVitDiu = nutrientVitDiu;
	}
	public String getNutrientVitK() {
		return nutrientVitK;
	}
	public void setNutrientVitK(String nutrientVitK) {
		this.nutrientVitK = nutrientVitK;
	}
	public String getNutrientFaSat() {
		return nutrientFaSat;
	}
	public void setNutrientFaSat(String nutrientFaSat) {
		this.nutrientFaSat = nutrientFaSat;
	}
	public String getNutrientFaMono() {
		return nutrientFaMono;
	}
	public void setNutrientFaMono(String nutrientFaMono) {
		this.nutrientFaMono = nutrientFaMono;
	}
	public String getNutrientFaPoly() {
		return nutrientFaPoly;
	}
	public void setNutrientFaPoly(String nutrientFaPoly) {
		this.nutrientFaPoly = nutrientFaPoly;
	}
	public String getNutrientCholestrl() {
		return nutrientCholestrl;
	}
	public void setNutrientCholestrl(String nutrientCholestrl) {
		this.nutrientCholestrl = nutrientCholestrl;
	}
	public String getNutrientGmWt1() {
		return nutrientGmWt1;
	}
	public void setNutrientGmWt1(String nutrientGmWt1) {
		this.nutrientGmWt1 = nutrientGmWt1;
	}
	public String getNutrientGmWtDesc1() {
		return nutrientGmWtDesc1;
	}
	public void setNutrientGmWtDesc1(String nutrientGmWtDesc1) {
		this.nutrientGmWtDesc1 = nutrientGmWtDesc1;
	}
	public String getNutrientGmWt2() {
		return nutrientGmWt2;
	}
	public void setNutrientGmWt2(String nutrientGmWt2) {
		this.nutrientGmWt2 = nutrientGmWt2;
	}
	public String getNutrientGmWtDesc2() {
		return nutrientGmWtDesc2;
	}
	public void setNutrientGmWtDesc2(String nutrientGmWtDesc2) {
		this.nutrientGmWtDesc2 = nutrientGmWtDesc2;
	}
	public String getNutrientRefusePct() {
		return nutrientRefusePct;
	}
	public void setNutrientRefusePct(String nutrientRefusePct) {
		this.nutrientRefusePct = nutrientRefusePct;
	}
	public float getFoodWeight() {
		return foodWeight;
	}
	public void setFoodWeight(float foodWeight) {
		this.foodWeight = foodWeight;
	}
	
	
	
}
