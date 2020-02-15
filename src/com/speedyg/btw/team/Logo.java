package com.speedyg.btw.team;

/**
 * This class created for Basic Team Wars plugin.
 *
 * @author Ozgen, Yusuf Serhat
 * @version 1.0.0-ALFA
 * @since 18.12.2019
 */

public class Logo {

    private short[][] logo;

    public Logo(short[][] logo) {
        this.logo = logo;
    }

    private void fillBlank() {
        for (short i = 0; i < logo.length; i++) {
            for (short j = 0; j < logo.length; j++) {
                logo[i][j] = 0;
            }
        }
    }

    public Logo() {
        logo = new short[10][10];
        fillBlank();
    }

    public short[][] getLogo() {
        return this.logo;
    }

    public void setLogo(short[][] logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        String don = "";
        if (logo != null)
            for (short i = 0; i < logo.length; i++) {
                for (short j = 0; j < logo.length; j++) {
                    if (j == logo.length - 1)
                        don += logo[i][j] + "";
                    else
                        don += logo[i][j] + ",";
                }
                if (i == logo.length - 1)
                    don += "";
                else
                    don += "-";
            }
        return don;
    }

    public static Logo stringToLogo(String logoString) {
        short[][] logo = new short[10][10];
        Logo returnLogo = new Logo();
        if (logoString != null)
            if (logoString.length() > 0 && logoString.contains("-") && logoString.contains(",")) {
                String[] iStr = logoString.split("-");
                for (short i = 0; i < iStr.length; i++) {
                    String[] jStr = iStr[i].split(",");
                    for (short j = 0; j < jStr.length; j++) {
                        logo[i][j] = Short.parseShort(jStr[j]);
                    }
                }
                returnLogo.setLogo(logo);
            }

        return returnLogo;
    }

}
