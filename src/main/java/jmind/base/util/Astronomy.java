package jmind.base.util;

public enum Astronomy {

    Aries, //白羊座
    Taurus, //金牛座
    Gemini, //双子座
    Cancer, //巨蟹座
    Leo, //狮子座
    Virgo, //处女座
    Libra, //天秤座
    Scorpio, //天蝎座
    Sagittarius, //射手座
    Capricorn, //摩羯座
    Aquarius, //水瓶座
    Pisces, //双鱼座
    Error;

    // 相配星座
    public Astronomy[] getMatchedAstronomy() {
        switch (this) {
        case Aries:
            return new Astronomy[] { Leo, Sagittarius, Libra };
        case Taurus:
            return new Astronomy[] { Capricorn, Virgo, Taurus };
        case Gemini:
            return new Astronomy[] { Libra, Leo, Aquarius };
        case Cancer:
            return new Astronomy[] { Scorpio, Pisces, Virgo };
        case Leo:
            return new Astronomy[] { Aries, Gemini, Sagittarius };
        case Virgo:
            return new Astronomy[] { Taurus, Capricorn, Pisces };
        case Libra:
            return new Astronomy[] { Gemini, Aquarius, Aries };
        case Scorpio:
            return new Astronomy[] { Pisces, Cancer, Virgo };
        case Sagittarius:
            return new Astronomy[] { Leo, Aries, Sagittarius };
        case Capricorn:
            return new Astronomy[] { Taurus, Virgo, Capricorn };
        case Aquarius:
            return new Astronomy[] { Gemini, Libra, Aquarius };
        case Pisces:
            return new Astronomy[] { Scorpio, Cancer, Virgo };
        default:
            return new Astronomy[] { Error };
        }
    }

    public String[] getStartAndEndDate() {
        switch (this) {
        case Aries:
            return new String[] { "0321", "0420" };
        case Taurus:
            return new String[] { "0421", "0520" };
        case Gemini:
            return new String[] { "0521", "0621" };
        case Cancer:
            return new String[] { "0622", "0722" };
        case Leo:
            return new String[] { "0723", "0822" };
        case Virgo:
            return new String[] { "0823", "0922" };
        case Libra:
            return new String[] { "0923", "1022" };
        case Scorpio:
            return new String[] { "1023", "1121" };
        case Sagittarius:
            return new String[] { "1122", "1221" };
        case Capricorn:
            return new String[] { "1222", "0119" };
        case Aquarius:
            return new String[] { "0120", "0218" };
        case Pisces:
            return new String[] { "0219", "0320" };
        default:
            return new String[] { "0000", "0000" };
        }
    }

    public String toString() {
        switch (this) {
        case Aries:
            return "白羊座";
        case Taurus:
            return "金牛座";
        case Gemini:
            return "双子座";
        case Cancer:
            return "巨蟹座";
        case Leo:
            return "狮子座";
        case Virgo:
            return "处女座";
        case Libra:
            return "天秤座";
        case Scorpio:
            return "天蝎座";
        case Sagittarius:
            return "射手座";
        case Capricorn:
            return "摩羯座";
        case Aquarius:
            return "水瓶座";
        case Pisces:
            return "双鱼座";
        default:
            return "";
        }
    }

    public static Astronomy getAstronomy(int month, int day) {
        switch (month) {
        case 1:
            return day < 20 ? Capricorn : Aquarius;
        case 2:
            return day < 19 ? Aquarius : Pisces;
        case 3:
            return day < 21 ? Pisces : Aries;
        case 4:
            return day < 21 ? Aries : Taurus;
        case 5:
            return day < 21 ? Taurus : Gemini;
        case 6:
            return day < 22 ? Gemini : Cancer;
        case 7:
            return day < 23 ? Cancer : Leo;
        case 8:
            return day < 23 ? Leo : Virgo;
        case 9:
            return day < 23 ? Virgo : Libra;
        case 10:
            return day < 23 ? Libra : Scorpio;
        case 11:
            return day < 22 ? Scorpio : Sagittarius;
        case 12:
            return day < 22 ? Sagittarius : Capricorn;
        default:
            return Error;
        }
    }

    public static Astronomy getAstronomy(String name) {
        if (DataUtil.isEmpty(name))
            return Error;
        if (name.startsWith("白羊"))
            return Aries;
        if (name.startsWith("金牛"))
            return Taurus;
        if (name.startsWith("双子"))
            return Gemini;
        if (name.startsWith("巨蟹"))
            return Cancer;
        if (name.startsWith("狮子"))
            return Leo;
        if (name.startsWith("处女"))
            return Virgo;
        if (name.startsWith("天秤"))
            return Libra;
        if (name.startsWith("天蝎"))
            return Scorpio;
        if (name.startsWith("射手"))
            return Sagittarius;
        if (name.startsWith("摩羯"))
            return Capricorn;
        if (name.startsWith("水瓶"))
            return Aquarius;
        if (name.startsWith("双鱼"))
            return Pisces;
        return Error;
    }

}
