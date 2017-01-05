package com.lefu.es.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 营养成分各个属性
 * @author Leon
 * 2015-11-17
 */
public class NutrientAtrrBo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NutrientAtrrBo(String attrName,String attrValue){
		this.attrName = attrName;
		this.attrValue = attrValue;
	}
	
	private String attrName;
	private String attrValue;
	
	public String getAttrName() {
		return attrName;
	}
	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
	public String getAttrValue() {
		return attrValue;
	}
	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}
	
	
	public static List<NutrientAtrrBo> formatNutrientAtrrBo(NutrientBo nutrientBo){
		if(null==nutrientBo)return null;
		
		List<NutrientAtrrBo> naNutrientAtrrBos = new ArrayList<NutrientAtrrBo>();
		
		NutrientAtrrBo nutrientAtrrBo1 = new NutrientAtrrBo("Water(g)", nutrientBo.getNutrientWater());
		NutrientAtrrBo nutrientAtrrBo2 = new NutrientAtrrBo("Energ(kcal)", nutrientBo.getNutrientEnerg());
		NutrientAtrrBo nutrientAtrrBo3 = new NutrientAtrrBo("Protein(g)", nutrientBo.getNutrientProtein());
		NutrientAtrrBo nutrientAtrrBo4 = new NutrientAtrrBo("Ash(g)", nutrientBo.getNutrientAsh());
		NutrientAtrrBo nutrientAtrrBo5 = new NutrientAtrrBo("Carbohydrt(g)", nutrientBo.getNutrientCarbohydrt());
		NutrientAtrrBo nutrientAtrrBo6 = new NutrientAtrrBo("Fiber_TD(g)", nutrientBo.getNutrientFiberTD());
		NutrientAtrrBo nutrientAtrrBo7 = new NutrientAtrrBo("Sugar_Tot(g)", nutrientBo.getNutrientSugarTot());
		NutrientAtrrBo nutrientAtrrBo8 = new NutrientAtrrBo("Calcium(mg)", nutrientBo.getNutrientCalcium());
		NutrientAtrrBo nutrientAtrrBo9 = new NutrientAtrrBo("Iron(mg)", nutrientBo.getNutrientIron());
		NutrientAtrrBo nutrientAtrrBo10 = new NutrientAtrrBo("Magnesium(mg)", nutrientBo.getNutrientMagnesium());
		NutrientAtrrBo nutrientAtrrBo11 = new NutrientAtrrBo("Phosphorus(mg)", nutrientBo.getNutrientPhosphorus());
		NutrientAtrrBo nutrientAtrrBo12 = new NutrientAtrrBo("Potassium(mg)", nutrientBo.getNutrientPotassium());
		NutrientAtrrBo nutrientAtrrBo13 = new NutrientAtrrBo("Sodium(mg)", nutrientBo.getNutrientSodium());
		NutrientAtrrBo nutrientAtrrBo14 = new NutrientAtrrBo("Zinc(mg)", nutrientBo.getNutrientZinc());
		NutrientAtrrBo nutrientAtrrBo15 = new NutrientAtrrBo("Copper(mg)", nutrientBo.getNutrientCopper());
		NutrientAtrrBo nutrientAtrrBo16 = new NutrientAtrrBo("Manganese(mg)", nutrientBo.getNutrientManganese());
		NutrientAtrrBo nutrientAtrrBo17 = new NutrientAtrrBo("Selenium(µg)", nutrientBo.getNutrientSelenium());
		NutrientAtrrBo nutrientAtrrBo18 = new NutrientAtrrBo("Vit_C(mg)", nutrientBo.getNutrientVitc());
		NutrientAtrrBo nutrientAtrrBo19 = new NutrientAtrrBo("Thiamin(mg)", nutrientBo.getNutrientThiamin());
		NutrientAtrrBo nutrientAtrrBo20 = new NutrientAtrrBo("Riboflavin(mg)", nutrientBo.getNutrientRiboflavin());
		NutrientAtrrBo nutrientAtrrBo21 = new NutrientAtrrBo("Niacin(mg)", nutrientBo.getNutrientNiacin());
		NutrientAtrrBo nutrientAtrrBo22 = new NutrientAtrrBo("Panto_Acid(mg)", nutrientBo.getNutrientPantoAcid());
		NutrientAtrrBo nutrientAtrrBo23 = new NutrientAtrrBo("Vit_B6(mg)", nutrientBo.getNutrientVitB6());
		NutrientAtrrBo nutrientAtrrBo24 = new NutrientAtrrBo("Folate_Tot(µg)", nutrientBo.getNutrientFolateTot());
		NutrientAtrrBo nutrientAtrrBo25 = new NutrientAtrrBo("Folic_Acid(µg)", nutrientBo.getNutrientFolicAcid());
		NutrientAtrrBo nutrientAtrrBo26 = new NutrientAtrrBo("Food_Folate(µg)", nutrientBo.getNutrientFoodFolate());
		NutrientAtrrBo nutrientAtrrBo27 = new NutrientAtrrBo("Folate_DFE(µg)", nutrientBo.getNutrientFolateDfe());
		NutrientAtrrBo nutrientAtrrBo28 = new NutrientAtrrBo("Choline_Tot(µg)", nutrientBo.getNutrientCholineTot());
		NutrientAtrrBo nutrientAtrrBo29 = new NutrientAtrrBo("Vit_B12(µg)", nutrientBo.getNutrientVitB12());
		NutrientAtrrBo nutrientAtrrBo30 = new NutrientAtrrBo("Vit_A_IU(µg)", nutrientBo.getNutrientVitAiu());
		NutrientAtrrBo nutrientAtrrBo31 = new NutrientAtrrBo("Vit_A_RAE(µg)", nutrientBo.getNutrientVitArae());
		NutrientAtrrBo nutrientAtrrBo32 = new NutrientAtrrBo("Retinol(µg)", nutrientBo.getNutrientRetinol());
		NutrientAtrrBo nutrientAtrrBo33 = new NutrientAtrrBo("Alpha_Carot(µg)", nutrientBo.getNutrientAlphaCarot());
		NutrientAtrrBo nutrientAtrrBo34 = new NutrientAtrrBo("Beta_Carot(µg)", nutrientBo.getNutrientBetaCarot());
		NutrientAtrrBo nutrientAtrrBo35 = new NutrientAtrrBo("Beta_Crypt(µg)", nutrientBo.getNutrientBetaCrypt());
		NutrientAtrrBo nutrientAtrrBo36 = new NutrientAtrrBo("Lycopene(µg)", nutrientBo.getNutrientLycopene());
		NutrientAtrrBo nutrientAtrrBo37 = new NutrientAtrrBo("Lut+Zea(µg)", nutrientBo.getNutrientLutZea());
		NutrientAtrrBo nutrientAtrrBo38 = new NutrientAtrrBo("Vit_E(µg)", nutrientBo.getNutrientVite());
		NutrientAtrrBo nutrientAtrrBo39 = new NutrientAtrrBo("Vit_D(µg)", nutrientBo.getNutrientVitd());
		NutrientAtrrBo nutrientAtrrBo40 = new NutrientAtrrBo("Vit_D_IU(µg)", nutrientBo.getNutrientVitDiu());
		NutrientAtrrBo nutrientAtrrBo41 = new NutrientAtrrBo("Vit_K(µg)", nutrientBo.getNutrientVitK());
		NutrientAtrrBo nutrientAtrrBo42 = new NutrientAtrrBo("FA_Sat(g)", nutrientBo.getNutrientFaSat());
		NutrientAtrrBo nutrientAtrrBo43 = new NutrientAtrrBo("FA_Mono(g)", nutrientBo.getNutrientFaMono());
		NutrientAtrrBo nutrientAtrrBo44 = new NutrientAtrrBo("FA_Poly(g)", nutrientBo.getNutrientFaPoly());
		NutrientAtrrBo nutrientAtrrBo45 = new NutrientAtrrBo("Cholestrl(mg)", nutrientBo.getNutrientCholestrl());
		NutrientAtrrBo nutrientAtrrBo46 = new NutrientAtrrBo("GmWt_1", nutrientBo.getNutrientGmWt1());
		NutrientAtrrBo nutrientAtrrBo47 = new NutrientAtrrBo("GmWt_2", nutrientBo.getNutrientGmWt2());
		NutrientAtrrBo nutrientAtrrBo48 = new NutrientAtrrBo("Refuse_Pct", nutrientBo.getNutrientRefusePct());

		
		naNutrientAtrrBos.add(nutrientAtrrBo1);
		naNutrientAtrrBos.add(nutrientAtrrBo2);
		naNutrientAtrrBos.add(nutrientAtrrBo3);
		naNutrientAtrrBos.add(nutrientAtrrBo4);
		naNutrientAtrrBos.add(nutrientAtrrBo5);
		naNutrientAtrrBos.add(nutrientAtrrBo6);
		naNutrientAtrrBos.add(nutrientAtrrBo7);
		naNutrientAtrrBos.add(nutrientAtrrBo8);
		naNutrientAtrrBos.add(nutrientAtrrBo9);
		naNutrientAtrrBos.add(nutrientAtrrBo10);
		
		naNutrientAtrrBos.add(nutrientAtrrBo11);
		naNutrientAtrrBos.add(nutrientAtrrBo12);
		naNutrientAtrrBos.add(nutrientAtrrBo13);
		naNutrientAtrrBos.add(nutrientAtrrBo14);
		naNutrientAtrrBos.add(nutrientAtrrBo15);
		naNutrientAtrrBos.add(nutrientAtrrBo16);
		naNutrientAtrrBos.add(nutrientAtrrBo17);
		naNutrientAtrrBos.add(nutrientAtrrBo18);
		naNutrientAtrrBos.add(nutrientAtrrBo19);
		naNutrientAtrrBos.add(nutrientAtrrBo20);
		
		naNutrientAtrrBos.add(nutrientAtrrBo21);
		naNutrientAtrrBos.add(nutrientAtrrBo22);
		naNutrientAtrrBos.add(nutrientAtrrBo23);
		naNutrientAtrrBos.add(nutrientAtrrBo24);
		naNutrientAtrrBos.add(nutrientAtrrBo25);
		naNutrientAtrrBos.add(nutrientAtrrBo26);
		naNutrientAtrrBos.add(nutrientAtrrBo27);
		naNutrientAtrrBos.add(nutrientAtrrBo28);
		naNutrientAtrrBos.add(nutrientAtrrBo29);
		naNutrientAtrrBos.add(nutrientAtrrBo30);
		
		naNutrientAtrrBos.add(nutrientAtrrBo31);
		naNutrientAtrrBos.add(nutrientAtrrBo32);
		naNutrientAtrrBos.add(nutrientAtrrBo33);
		naNutrientAtrrBos.add(nutrientAtrrBo34);
		naNutrientAtrrBos.add(nutrientAtrrBo35);
		naNutrientAtrrBos.add(nutrientAtrrBo36);
		naNutrientAtrrBos.add(nutrientAtrrBo37);
		naNutrientAtrrBos.add(nutrientAtrrBo38);
		naNutrientAtrrBos.add(nutrientAtrrBo39);
		naNutrientAtrrBos.add(nutrientAtrrBo40);
		
		naNutrientAtrrBos.add(nutrientAtrrBo41);
		naNutrientAtrrBos.add(nutrientAtrrBo42);
		naNutrientAtrrBos.add(nutrientAtrrBo43);
		naNutrientAtrrBos.add(nutrientAtrrBo44);
		naNutrientAtrrBos.add(nutrientAtrrBo45);
		naNutrientAtrrBos.add(nutrientAtrrBo46);
		naNutrientAtrrBos.add(nutrientAtrrBo47);
		naNutrientAtrrBos.add(nutrientAtrrBo48);

		return naNutrientAtrrBos;
	}
	
}
