package com.houvven.guise.module.preset

import com.houvven.guise.module.PresetAdapter


/**
 * Enumerates predefined SIM card / mobile carrier configurations for identity spoofing.
 *
 * Each constant represents a real-world mobile carrier and maps a human-readable name
 * ([label]) to a structured value string ([value]) with the format `carrierName:mccMnc:countryCode`.
 * These presets are used by the Xposed hook framework to override SIM card information
 * reported to target applications, enabling users to simulate a carrier identity from
 * a different region.
 *
 * **Value format:** `name:code:country` where:
 * - `name` is the carrier's display name
 * - `code` is the MCC+MNC (Mobile Country Code + Mobile Network Code) string
 * - `country` is the ISO 3166-1 alpha-2 country code
 *
 * Covers carriers across Asia-Pacific (China, Hong Kong, Taiwan, Singapore, Japan),
 * North America (US), and Europe (UK, Germany, France, Italy, Spain, Netherlands,
 * Scandinavia, Belgium, Poland, Czech Republic).
 *
 * Implements [PresetAdapter] so instances can be directly rendered in UI selection
 * components (e.g., spinners, dropdown menus).
 *
 * @see PresetAdapter
 */
enum class SimPreset(
    override val label: String,
    /**
     * Structured SIM carrier value in the format `name:code:country`.
     *
     * - `name`: carrier display name
     * - `code`: MCC+MNC identifier
     * - `country`: ISO 3166-1 alpha-2 country code
     */
    override val value: String
) : PresetAdapter {

    // ── China ─────────────────────────────────────────────────────────────────

    /** @property ChinaTelecom China Telecom (China), MCC/MNC: 46003. */
    ChinaTelecom("中国电信", "中国电信:46003:CN"),

    /** @property ChinaUnicom China Unicom (China), MCC/MNC: 46001. */
    ChinaUnicom("中国联通", "中国联通:46001:CN"),

    /** @property ChinaMobile China Mobile (China), MCC/MNC: 46000. */
    ChinaMobile("中国移动", "中国移动:46000:CN"),

    // ── Hong Kong / Macau / Taiwan ────────────────────────────────────────────

    /** @property OneTwoFree 12Free (Hong Kong), MCC/MNC: 46011. */
    OneTwoFree("香港12Free", "12Free:46011:CN"),

    /** @property CTM CTM (Macau), MCC/MNC: 45502. */
    CTM("澳门CTM", "CTM:45502:CN"),

    /** @property CSL CSL (Hong Kong), MCC/MNC: 45412. */
    CSL("香港CSL", "CSL:45412:CN"),

    /** @property HKT HKT (Hong Kong), MCC/MNC: 45407. */
    HKT("香港HTK", "HKT:45407:CN"),

    /** @property ChunghwaTelecom Chunghwa Telecom (Taiwan), MCC/MNC: 46697. */
    ChunghwaTelecom("台湾中华电信", "中华电信:46697:CN"),

    /** @property FarEasTone Far EasTone (Taiwan), MCC/MNC: 46692. */
    FarEasTone("台湾遠傳", "遠傳:46692:CN"),

    /** @property TaiwanMobile Taiwan Mobile (Taiwan), MCC/MNC: 46601. */
    TaiwanMobile("台湾台灣大哥大", "台灣大哥大:46601:CN"),

    /** @property TaiwanStar Taiwan Star (Taiwan), MCC/MNC: 46605. */
    TaiwanStar("台湾台灣之星", "台灣之星:46605:CN"),

    /** @property TStar Asia Pacific Telecom (Taiwan), MCC/MNC: 46602. */
    TStar("台湾亞太電信", "亞太電信:46602:CN"),

    /** @property Smartone SmarTone (Hong Kong), MCC/MNC: 45418. */
    Smartone("香港智能One", "智能One:45418:CN"),

    // ── Singapore ─────────────────────────────────────────────────────────────

    /** @property SingTel Singtel (Singapore), MCC/MNC: 52501. */
    SingTel("新加坡星展", "星展:52501:CN"),

    // ── Japan ─────────────────────────────────────────────────────────────────

    /** @property KDDI KDDI (Japan), MCC/MNC: 44020. */
    KDDI("日本KDDI", "KDDI:44020:JP"),

    /** @property SoftBank SoftBank (Japan), MCC/MNC: 44050. */
    SoftBank("日本SoftBank", "SoftBank:44050:JP"),

    // ── United States ─────────────────────────────────────────────────────────

    /** @property Verizon Verizon (United States), MCC/MNC: 31000. */
    Verizon("美国Verizon", "Verizon:31000:US"),

    /** @property ATandT AT&T (United States), MCC/MNC: 31000. */
    ATandT("美国AT&T", "AT&T:31000:US"),

    /** @property TMobile T-Mobile (United States), MCC/MNC: 31000. */
    TMobile("美国T-Mobile", "T-Mobile:31000:US"),

    /** @property Sprint Sprint (United States), MCC/MNC: 31000. */
    Sprint("美国Sprint", "Sprint:31000:US"),

    // ── United Kingdom ────────────────────────────────────────────────────────

    /** @property Vodafone Vodafone (United Kingdom), MCC/MNC: 23415. */
    Vodafone("英国Vodafone", "Vodafone:23415:GB"),

    /** @property O2 O2 (United Kingdom), MCC/MNC: 23415. */
    O2("英国O2", "O2:23415:GB"),

    /** @property EE EE (United Kingdom), MCC/MNC: 23415. */
    EE("英国EE", "EE:23415:GB"),

    /** @property ThreeUK Three UK (United Kingdom), MCC/MNC: 23415. */
    ThreeUK("英国ThreeUK", "ThreeUK:23415:GB"),

    // ── Germany ───────────────────────────────────────────────────────────────

    /** @property VodafoneDE Vodafone (Germany), MCC/MNC: 26201. */
    VodafoneDE("德国Vodafone", "Vodafone:26201:DE"),

    /** @property TelekomDE Telekom (Germany), MCC/MNC: 26201. */
    TelekomDE("德国Telekom", "Telekom:26201:DE"),

    /** @property O2DE O2 (Germany), MCC/MNC: 26201. */
    O2DE("德国O2", "O2:26201:DE"),

    /** @property TMobileDE T-Mobile (Germany), MCC/MNC: 26201. */
    TMobileDE("德国T-Mobile", "T-Mobile:26201:DE"),

    // ── France ────────────────────────────────────────────────────────────────

    /** @property OrangeFR Orange (France), MCC/MNC: 20801. */
    OrangeFR("法国Orange", "Orange:20801:FR"),

    /** @property SFRFR SFR (France), MCC/MNC: 20801. */
    SFRFR("法国SFR", "SFR:20801:FR"),

    /** @property BouyguesFR Bouygues Telecom (France), MCC/MNC: 20801. */
    BouyguesFR("法国Bouygues", "Bouygues:20801:FR"),

    /** @property TelekomFR Telekom (France), MCC/MNC: 20801. */
    TelekomFR("法国Telekom", "Telekom:20801:FR"),

    // ── Italy ─────────────────────────────────────────────────────────────────

    /** @property VodafoneIT Vodafone (Italy), MCC/MNC: 22201. */
    VodafoneIT("意大利Vodafone", "Vodafone:22201:IT"),

    /** @property TIMIT TIM (Italy), MCC/MNC: 22201. */
    TIMIT("意大利TIM", "TIM:22201:IT"),

    /** @property WindIT Wind (Italy), MCC/MNC: 22201. */
    WindIT("意大利Wind", "Wind:22201:IT"),

    /** @property TelekomIT Telekom (Italy), MCC/MNC: 22201. */
    TelekomIT("意大利Telekom", "Telekom:22201:IT"),

    // ── Spain ─────────────────────────────────────────────────────────────────

    /** @property VodafoneES Vodafone (Spain), MCC/MNC: 21401. */
    VodafoneES("西班牙Vodafone", "Vodafone:21401:ES"),

    /** @property OrangeES Orange (Spain), MCC/MNC: 21401. */
    OrangeES("西班牙Orange", "Orange:21401:ES"),

    /** @property TelekomES Telekom (Spain), MCC/MNC: 21401. */
    TelekomES("西班牙Telekom", "Telekom:21401:ES"),

    // ── Netherlands ───────────────────────────────────────────────────────────

    /** @property VodafoneNL Vodafone (Netherlands), MCC/MNC: 20404. */
    VodafoneNL("荷兰Vodafone", "Vodafone:20404:NL"),

    /** @property KPNNL KPN (Netherlands), MCC/MNC: 20404. */
    KPNNL("荷兰KPN", "KPN:20404:NL"),

    /** @property TMobileNL T-Mobile (Netherlands), MCC/MNC: 20404. */
    TMobileNL("荷兰T-Mobile", "T-Mobile:20404:NL"),

    /** @property TelekomNL Telekom (Netherlands), MCC/MNC: 20404. */
    TelekomNL("荷兰Telekom", "Telekom:20404:NL"),

    // ── Scandinavia ───────────────────────────────────────────────────────────

    /** @property VodafoneSE Vodafone (Sweden), MCC/MNC: 24001. */
    VodafoneSE("瑞典Vodafone", "Vodafone:24001:SE"),

    /** @property TelekomSE Telekom (Sweden), MCC/MNC: 24001. */
    TelekomSE("瑞典Telekom", "Telekom:24001:SE"),

    /** @property TelenorSE Telenor (Sweden), MCC/MNC: 24001. */
    TelenorSE("瑞典Telenor", "Telenor:24001:SE"),

    /** @property ThreeSE Three (Sweden), MCC/MNC: 24001. */
    ThreeSE("瑞典Three", "Three:24001:SE"),

    /** @property VodafoneDK Vodafone (Denmark), MCC/MNC: 23801. */
    VodafoneDK("丹麦Vodafone", "Vodafone:23801:DK"),

    /** @property TDCDK TDC (Denmark), MCC/MNC: 23801. */
    TDCDK("丹麦TDC", "TDC:23801:DK"),

    /** @property TelekomDK Telekom (Denmark), MCC/MNC: 23801. */
    TelekomDK("丹麦Telekom", "Telekom:23801:DK"),

    /** @property TeliaDK Telia (Denmark), MCC/MNC: 23801. */
    TeliaDK("丹麦Telia", "Telia:23801:DK"),

    /** @property VodafoneFI Vodafone (Finland), MCC/MNC: 24412. */
    VodafoneFI("芬兰Vodafone", "Vodafone:24412:FI"),

    /** @property TelekomFI Telekom (Finland), MCC/MNC: 24412. */
    TelekomFI("芬兰Telekom", "Telekom:24412:FI"),

    /** @property ElisaFI Elisa (Finland), MCC/MNC: 24412. */
    ElisaFI("芬兰Elisa", "Elisa:24412:FI"),

    /** @property DNAFI DNA (Finland), MCC/MNC: 24412. */
    DNAFI("芬兰DNA", "DNA:24412:FI"),

    /** @property VodafoneNO Vodafone (Norway), MCC/MNC: 24201. */
    VodafoneNO("挪威Vodafone", "Vodafone:24201:NO"),

    /** @property TelenorNO Telenor (Norway), MCC/MNC: 24201. */
    TelenorNO("挪威Telenor", "Telenor:24201:NO"),

    /** @property TelekomNO Telekom (Norway), MCC/MNC: 24201. */
    TelekomNO("挪威Telekom", "Telekom:24201:NO"),

    /** @property NetComNO NetCom (Norway), MCC/MNC: 24201. */
    NetComNO("挪威NetCom", "NetCom:24201:NO"),

    // ── Belgium ───────────────────────────────────────────────────────────────

    /** @property VodafoneBE Vodafone (Belgium), MCC/MNC: 20610. */
    VodafoneBE("比利时Vodafone", "Vodafone:20610:BE"),

    /** @property ProximusBE Proximus (Belgium), MCC/MNC: 20610. */
    ProximusBE("比利时Proximus", "Proximus:20610:BE"),

    /** @property OrangeBE Orange (Belgium), MCC/MNC: 20610. */
    OrangeBE("比利时Orange", "Orange:20610:BE"),

    /** @property TMobileBE T-Mobile (Belgium), MCC/MNC: 20610. */
    TMobileBE("比利时T-Mobile", "T-Mobile:20610:BE"),

    /** @property TelekomBE Telekom (Belgium), MCC/MNC: 20610. */
    TelekomBE("比利时Telekom", "Telekom:20610:BE"),

    // ── Poland ────────────────────────────────────────────────────────────────

    /** @property VodafonePL Vodafone (Poland), MCC/MNC: 26002. */
    VodafonePL("波兰Vodafone", "Vodafone:26002:PL"),

    /** @property TMobilePL T-Mobile (Poland), MCC/MNC: 26002. */
    TMobilePL("波兰T-Mobile", "T-Mobile:26002:PL"),

    /** @property OrangePL Orange (Poland), MCC/MNC: 26002. */
    OrangePL("波兰Orange", "Orange:26002:PL"),

    /** @property PlayPL Play (Poland), MCC/MNC: 26002. */
    PlayPL("波兰Play", "Play:26002:PL"),

    /** @property TelekomPL Telekom (Poland), MCC/MNC: 26002. */
    TelekomPL("波兰Telekom", "Telekom:26002:PL"),

    // ── Czech Republic ────────────────────────────────────────────────────────

    /** @property VodafoneCZ Vodafone (Czech Republic), MCC/MNC: 23001. */
    VodafoneCZ("捷克Vodafone", "Vodafone:23001:CZ"),

    /** @property TelekomCZ Telekom (Czech Republic), MCC/MNC: 23001. */
    TelekomCZ("捷克Telekom", "Telekom:23001:CZ"),

    ;

}
