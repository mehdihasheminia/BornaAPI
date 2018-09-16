package com.bornaapp.borna2d;

/**
 * Created by Hashemi on 31/01/2018 .<br>
 * desc:
 * <p>
 * more info:
 */

public class Farsi {

    /**
     * Change text direction to right-to-left. Characters from ARABIC_STANDARD_CHARS will be replaced
     * with corresponding character from ARABIC_PRESENTATION_B_CHARS.
     *
     * @param src input string to be corrected
     * @return returns corrected string
     */
    public String CompatibilityString(final String src) {

        CharDef[] charDefs = new CharDef[src.length()];

        //if there's no farsi chars, flag this to prevent right-to-left flip
        boolean isPureEnglish = true;

        // First iteration: Processing the string and extract character parameters
        //
        for (int i = 0; i < src.length(); i++) {
            charDefs[i] = new CharDef();
            char currentChar = src.charAt(i);

            for (int j = 0; j < FarsiChars.length; j++) {
                if (currentChar == FarsiChars[j][0]) {
                    isPureEnglish = false;
                    //log.info("i=" + i + "character detected!. original=" + currentChar + " , in table=" + FarsiChars[j][0]);
                    charDefs[i].isFarsi = true;
                    charDefs[i].charID = j;
                    break;
                }
            }

            if (currentChar == AA[0] || currentChar == ALEF[0] || currentChar == DAAL[0] || currentChar == ZAAL[0]
                    || currentChar == REH[0] || currentChar == ZEH[0] || currentChar == ZHEH[0] || currentChar == VAAV[0]) {
                charDefs[i].isHalfAttached = true;
            }
        }

        // Second iteration: check character connectivity
        //
        StringBuilder corrected = new StringBuilder(src);

        for (int i = 0; i < src.length(); i++) {

            // don't process punctuation & unknown character
            if (!charDefs[i].isFarsi)
                continue;

            boolean beforeNotConnected = false;
            if (i == 0) { //first char
                beforeNotConnected = true;
            } else {
                if (!charDefs[i - 1].isFarsi || charDefs[i - 1].isHalfAttached) {
                    beforeNotConnected = true;
                }
            }

            boolean afterNotConnected = false;
            if (charDefs[i].isHalfAttached) {
                afterNotConnected = true;
            }
            if (i == src.length() - 1) {//last char
                afterNotConnected = true;
            } else {
                if (!charDefs[i + 1].isFarsi) { //or other punctuations and english characters
                    afterNotConnected = true;
                }
            }

            //0: separate letter, 1: starting letter, 2: middle letter, 3: ending letter
            int letterStatus = 0;
            if (beforeNotConnected && afterNotConnected)
                letterStatus = 0;
            if (beforeNotConnected && !afterNotConnected)
                letterStatus = 1;
            if (!beforeNotConnected && !afterNotConnected)
                letterStatus = 2;
            if (!beforeNotConnected && afterNotConnected)
                letterStatus = 3;

            //replace character with correct form
            //
            corrected.deleteCharAt(i);
            corrected.insert(i, FarsiChars[charDefs[i].charID][letterStatus]);
        }

        if (isPureEnglish)
            return corrected.toString();
        else
            return corrected.reverse().toString();  //right to left
    }

    private class CharDef {
        // it is a Farsi character, not english or punctuations
        boolean isFarsi = false;

        // What Farsi character it is?
        int charID = 0;

        // Is it one of those characters which attach only in one side?
        boolean isHalfAttached = false;
    }

    //------------------------------------ CHARACTER METHODS ---------------------------------------
    public static String ARABIC_STANDARD_CHARS() {
        String CHARS = "";
        //Standard Arabic
        // originally from 0x0600 to 0x06FF(256 chars)
        // but we use reduced version for efficiency
        for (int i = 0x0618; i <= 0x06FF; i++) {
            CHARS += new String(new int[]{i}, 0, 1);
        }
        return CHARS;
    }

    public static String ARABIC_PRESENTATION_A_CHARS() {
        String CHARS = "";
        //arabic presentation forms-a
        // originally from 0xFB50 to 0xFDFF(688 chars)
        // but we use reduced version for efficiency
        for (int i = 0xFB50; i <= 0xFB9D; i++) {
            CHARS += new String(new int[]{i}, 0, 1);
        }
        return CHARS;
    }

    public static String ARABIC_PRESENTATION_B_CHARS() {
        String CHARS = "";
        //arabic presentation forms-b (144 chars)
        for (int i = 0xFE70; i <= 0xFEFF; i++) {
            CHARS += new String(new int[]{i}, 0, 1);
        }
        return CHARS;
    }

    public static void PrintFarsiChars() {
        String output = "Farsi chars:\n";
        for (char[] FarsiChar : FarsiChars) {
            for (int j = 0; j < 4; j++) {
                output += StringOf(FarsiChar[j]) + " ";
            }
        }
        System.out.println(output);
    }

    public static void PrintArabicChars() {
        String output = "Arabic chars:\n"
                + Farsi.ARABIC_STANDARD_CHARS()
                + Farsi.ARABIC_PRESENTATION_A_CHARS()
                + Farsi.ARABIC_PRESENTATION_B_CHARS();
        System.out.println(output);
    }

    private static String StringOf(char codepoint) {
        return new String(new int[]{codepoint}, 0, 1);
    }

    // -------------------------------- Definition of FARSI chars ----------------------------------
    // every char is defined as a char array. first items is unicode code-point for ARABIC_STANDARD_CHARS
    // following items are code-points for starting letter, middle letter and ending letter and replace
    // standard chars.
    static final private char[] AA = {0x0622, 0x0622, 0x0622, 0x0622};
    static final private char[] ALEF = {0x0627, 0x0627, 0xFE8E, 0xFE8E};
    static final private char[] BEH = {0x0628, 0xFE91, 0xFE92, 0xFE90};
    static final private char[] PEH = {0x067E, 0xFB58, 0xFB59, 0xFB57};
    static final private char[] TEH = {0x062A, 0xFE97, 0xFE98, 0xFE96};
    static final private char[] THEH = {0x062B, 0xFE9B, 0xFE9C, 0xFE9A};
    static final private char[] JIM = {0x062C, 0xFE9F, 0xFEA0, 0xFE9E};
    static final private char[] CHEH = {0x0686, 0xFB7C, 0xFB7D, 0xFB7B};
    static final private char[] HAAH = {0x062D, 0xFEA3, 0xFEA4, 0xFEA2};
    static final private char[] KHEH = {0x062E, 0xFEA7, 0xFEA8, 0xFEA6};
    static final private char[] DAAL = {0x062F, 0x062F, 0xFEAA, 0xFEAA};
    static final private char[] ZAAL = {0x0630, 0x0630, 0xFEAC, 0xFEAC};
    static final private char[] REH = {0x0631, 0x0631, 0xFEAE, 0xFEAE};
    static final private char[] ZEH = {0x0632, 0x0632, 0xFEB0, 0xFEB0};
    static final private char[] ZHEH = {0x0698, 0x0698, 0xFB8B, 0xFB8B};
    static final private char[] SEEN = {0x0633, 0xFEB3, 0xFEB4, 0xFEB2};
    static final private char[] SHEEN = {0x0634, 0xFEB7, 0xFEB8, 0xFEB6};
    static final private char[] SAAD = {0x0635, 0xFEBB, 0xFEBC, 0xFEBA};
    static final private char[] ZAAT = {0x0636, 0xFEBF, 0xFEC0, 0xFEBE};
    static final private char[] TAH = {0x0637, 0x0637, 0xFEC2, 0xFEC2};
    static final private char[] ZAH = {0x0638, 0x0638, 0xFEC6, 0xFEC6};
    static final private char[] EIN = {0x0639, 0xFECB, 0xFECC, 0xFECA};
    static final private char[] QEIN = {0x063A, 0xFECF, 0xFED0, 0xFECE};
    static final private char[] FEH = {0x0641, 0xFED3, 0xFED4, 0xFED2};
    static final private char[] QAAF = {0x0642, 0xFED7, 0xFED8, 0xFED6};
    static final private char[] KAAF = {0x06A9, 0xFEDB, 0xFEDC, 0xFEDA};
    static final private char[] GAAF = {0x06AF, 0xFB94, 0xFB95, 0xFB93};
    static final private char[] LAAM = {0x0644, 0xFEDF, 0xFEE0, 0xFEDE};
    static final private char[] MEEM = {0x0645, 0xFEE3, 0xFEE4, 0xFEE2};
    static final private char[] NOON = {0x0646, 0xFEE7, 0xFEE8, 0xFEE6};
    static final private char[] VAAV = {0x0648, 0x0648, 0xFEEE, 0xFEEE};
    static final private char[] HEH = {0x0647, 0xFEEB, 0xFEEC, 0xFEEA};
    static final private char[] YEH = {0x06CC, 0xFEF3, 0xFEF4, 0xFEF0};
    static final private char[] Keshideh = {0x0640, 0x0640, 0x0640, 0x0640};

    static final private char[][] FarsiChars = {AA, ALEF, BEH, PEH, TEH, THEH, JIM, CHEH,
            HAAH, KHEH, DAAL, ZAAL, REH, ZEH, ZHEH, SEEN, SHEEN, SAAD, ZAAT, TAH, ZAH,
            EIN, QEIN, FEH, QAAF, KAAF, GAAF, LAAM, MEEM, NOON, VAAV, HEH, YEH, Keshideh};

    //todo: limited processed characters!
    // these are very limited characters to be processed. for example if a HAMZEH character
    // is passed by user, it wont be processed for its attachments
    // (as only listed chars are processed in CompatibilityString() )

    // todo: extended Hiero characters!
    // if we only add PrintFarsiChars() to hiero for rendering, if we face a special Arabic letter
    // like HAMZEH, etc... we will not see that in game. As a solution, we added PrintArabicChars()
    //to Hiero as well. Although they now will be rendered in game, their attachment is still ignored.
}