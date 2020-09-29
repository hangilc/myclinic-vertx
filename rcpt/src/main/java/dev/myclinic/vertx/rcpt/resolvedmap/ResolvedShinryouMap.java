package dev.myclinic.vertx.rcpt.resolvedmap;

import dev.myclinic.vertx.client.Service;
import dev.myclinic.vertx.mastermap.MasterNameMap;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class ResolvedShinryouMap extends ResolvedMapBase {

    @MasterNameMapAnnot(candidates = {"単純撮影（アナログ撮影）", "単純撮影（撮影）"})
    public int 単純撮影;
    @MasterNameMapAnnot(candidates = "単純撮影（イ）の写真診断")
    public int 単純撮影診断;
    @MasterNameMapAnnot(candidates = {"皮下、筋肉内注射", "皮内、皮下及び筋肉内注射"})
    public int 皮下筋注;
    @MasterNameMapAnnot(candidates = {"静脈内注射"})
    public int 静注;
    @MasterNameMapAnnot(candidates = {"初診", "初診料"})
    public int 初診;
    @MasterNameMapAnnot(candidates = {"再診（診療所）", "再診", "再診料"})
    public int 再診;
    @MasterNameMapAnnot(candidates = {"同日再診（診療所）", "同日再診", "同日再診料"})
    public int 同日再診;
    public int 外来管理加算;
    public int 往診;
    public int 尿一般;
    @MasterNameMapAnnot(candidates = {"潜血（便）", "糞便中ヘモグロビン定性", "Ｈｂ定性（便）", "Ｈｂ（便）"})
    public int 便潜血;
    @MasterNameMapAnnot(candidates = {"尿・糞便等検査判断料"})
    public int 尿便検査判断料;
    @MasterNameMapAnnot(candidates = {"血液学的検査判断料"})
    public int 血液検査判断料;
    @MasterNameMapAnnot(candidates = {"生化学的検査（１）判断料"})
    public int 生化Ⅰ判断料;
    @MasterNameMapAnnot(candidates = {"生化学的検査（２）判断料"})
    public int 生化Ⅱ判断料;
    @MasterNameMapAnnot(candidates = {"免疫学的検査判断料"})
    public int 免疫検査判断料;
    @MasterNameMapAnnot(candidates={"微生物学的検査判断料"})
    public int 微生物検査判断料;
    @MasterNameMapAnnot(candidates={"病理判断料","病理学的検査判断料"})
    public int 病理判断料;
    @MasterNameMapAnnot(candidates={"ＢーＶ","Ｂ－Ｖ"})
    public int 静脈採血;
    @MasterNameMapAnnot(candidates={"調剤料（内服薬・浸煎薬・屯服薬）"})
    public int 内服調剤;
    @MasterNameMapAnnot(candidates={"調剤料（外用薬）"})
    public int 外用調剤;
    @MasterNameMapAnnot(candidates={"ＥＣＧ１２"})
    public int 心電図;
    @MasterNameMapAnnot(candidates={"処方料（その他）"})
    public int 処方料;
    @MasterNameMapAnnot(candidates={"処方料（７種類以上内服薬又は向精神薬長期処方）","処方料（７種類以上）"})
    public int 処方料７;
    @MasterNameMapAnnot(candidates={"処方せん料（その他）","処方せん料（その他）（後発医薬品を含む）","処方箋料（その他）"})
    public int 処方せん料;
    @MasterNameMapAnnot(candidates={"処方せん料（７種類以上）","処方せん料（７種類以上）（後発医薬品を含む）","処方箋料（７種類以上内服薬又は向精神薬長期処方）"})
    public int 処方せん料７;
    @MasterNameMapAnnot(candidates={"調基（その他）"})
    public int 調基;
    @MasterNameMapAnnot(candidates={"特定疾患処方管理加算（処方料）","特定疾患処方管理加算１（処方料）"})
    public int 特定疾患処方;
    @MasterNameMapAnnot(candidates={"特定疾患処方管理加算（処方せん料）","特定疾患処方管理加算１（処方箋料）"})
    public int 特定疾患処方管理加算処方せん料;
    @MasterNameMapAnnot(candidates={"特定疾患処方管理加算２（処方料）","長期投薬加算（処方料）"})
    public int 長期処方;
    @MasterNameMapAnnot(candidates={"特定疾患処方管理加算２（処方箋料）","長期投薬加算（処方せん料）"})
    public int 長期投薬加算処方せん料;
    @MasterNameMapAnnot(candidates={"特定疾患療養指導料（診療所）","特定疾患療養管理料（診療所）"})
    public int 特定疾患管理;
    @MasterNameMapAnnot(candidates={"薬剤情報提供料"})
    public int 薬剤情報提供;
    @MasterNameMapAnnot(candidates={"後期高齢者薬剤情報提供料（手帳に記載する場合）","手帳記載加算（薬剤情報提供料）","老人薬剤情報提供料（健康手帳に記載する場合）"})
    public int 手帳記載加算;
    @MasterNameMapAnnot(candidates={"診療情報提供料（１）","診療情報提供料（Ａ）"})
    public int 診療情報提供料１;
    public int 訪問看護指示料;
    public int 療養費同意書交付料;
    @MasterNameMapAnnot(candidates={"調剤・処方料（麻・向・覚・毒）","調剤・処方料（麻薬等）"})
    public int 向精神薬;
    @MasterNameMapAnnot(candidates={"外来後発医薬品使用体制加算１","外来後発医薬品使用体制加算２"})
    public int 外来後発加算１;
    @MasterNameMapAnnot(candidates={"骨塩定量検査（ＭＤ法、ＳＥＸＡ法等）"})
    public int 骨塩定量ＭＤ法;
    @MasterNameMapAnnot(candidates={"末梢血液一般"})
    public int 血算;
    @MasterNameMapAnnot(candidates={"末梢血液像（自動機械法）", "像"})
    public int 末梢血液像;
    public int ＨｂＡ１ｃ;
    public int ＰＴ;
    @MasterNameMapAnnot(candidates={"ＡＳＴ","ＧＯＴ"})
    public int ＧＯＴ;
    @MasterNameMapAnnot(candidates={"ＡＬＴ","ＧＰＴ"})
    public int ＧＰＴ;
    @MasterNameMapAnnot(candidates={"γーＧＴＰ","γ－ＧＴ","γ－ＧＴＰ"})
    public int γＧＴＰ;
    @MasterNameMapAnnot(candidates={"ＣＫ","ＣＰＫ"})
    public int ＣＰＫ;
    public int クレアチニン;
    @MasterNameMapAnnot(candidates={"ＵＡ"})
    public int 尿酸;
    @MasterNameMapAnnot(candidates={"ＬＤＬーコレステロール","ＬＤＬ－コレステロール"})
    public int ＬＤＬコレステロール;
    @MasterNameMapAnnot(candidates={"ＨＤＬーＣｈ","ＨＤＬ－コレステロール","ＨＤＬ－Ｃｈ"})
    public int ＨＤＬコレステロール;
    public int ＴＧ;
    public int グルコース;
    @MasterNameMapAnnot(candidates={"カリウム","Ｋ"})
    public int カリウム;
    @MasterNameMapAnnot(candidates={"ＴＳＨ","ＴＳＨ精密"})
    public int ＴＳＨ;
    @MasterNameMapAnnot(candidates={"ＦＴ４","ＦＴ４精密"})
    public int ＦＴ４;
    @MasterNameMapAnnot(candidates={"ＦＴ３","ＦＴ３精密"})
    public int ＦＴ３;
    @MasterNameMapAnnot(candidates={"ＰＳＡ","ＰＳＡ精密"})
    public int ＰＳＡ;
    @MasterNameMapAnnot(candidates={"尿蛋白","蛋白定量（尿）"})
    public int 蛋白定量尿;
    @MasterNameMapAnnot(candidates={"クレアチニン（尿）"})
    public int クレアチニン尿;
    @MasterNameMapAnnot(candidates={"ＣＲＰ","ＣＲＰ（定量）"})
    public int ＣＲＰ;
    @MasterNameMapAnnot(candidates={"特異的ＩｇＥ","特異的ＩｇＥ半定量・定量"})
    public int 非特異的ＩｇＥ;

    public CompletableFuture<Void> resolveAt(LocalDate at){
        return resolveAt(at, Service.api::batchResolveShinryouNames);
    }

    @Override
    public String toString() {
        return "ResolvedShinryouMap{" +
                "単純撮影=" + 単純撮影 +
                ", 単純撮影診断=" + 単純撮影診断 +
                ", 皮下筋注=" + 皮下筋注 +
                ", 静注=" + 静注 +
                ", 初診=" + 初診 +
                ", 再診=" + 再診 +
                ", 同日再診=" + 同日再診 +
                ", 外来管理加算=" + 外来管理加算 +
                ", 往診=" + 往診 +
                ", 尿一般=" + 尿一般 +
                ", 便潜血=" + 便潜血 +
                ", 尿便検査判断料=" + 尿便検査判断料 +
                ", 血液検査判断料=" + 血液検査判断料 +
                ", 生化Ⅰ判断料=" + 生化Ⅰ判断料 +
                ", 生化Ⅱ判断料=" + 生化Ⅱ判断料 +
                ", 免疫検査判断料=" + 免疫検査判断料 +
                ", 微生物検査判断料=" + 微生物検査判断料 +
                ", 病理判断料=" + 病理判断料 +
                ", 静脈採血=" + 静脈採血 +
                ", 内服調剤=" + 内服調剤 +
                ", 外用調剤=" + 外用調剤 +
                ", 心電図=" + 心電図 +
                ", 処方料=" + 処方料 +
                ", 処方料７=" + 処方料７ +
                ", 処方せん料=" + 処方せん料 +
                ", 処方せん料７=" + 処方せん料７ +
                ", 調基=" + 調基 +
                ", 特定疾患処方=" + 特定疾患処方 +
                ", 特定疾患処方管理加算処方せん料=" + 特定疾患処方管理加算処方せん料 +
                ", 長期処方=" + 長期処方 +
                ", 長期投薬加算処方せん料=" + 長期投薬加算処方せん料 +
                ", 特定疾患管理=" + 特定疾患管理 +
                ", 薬剤情報提供=" + 薬剤情報提供 +
                ", 手帳記載加算=" + 手帳記載加算 +
                ", 診療情報提供料１=" + 診療情報提供料１ +
                ", 訪問看護指示料=" + 訪問看護指示料 +
                ", 療養費同意書交付料=" + 療養費同意書交付料 +
                ", 向精神薬=" + 向精神薬 +
                ", 外来後発加算１=" + 外来後発加算１ +
                ", 骨塩定量ＭＤ法=" + 骨塩定量ＭＤ法 +
                ", 血算=" + 血算 +
                ", 末梢血液像=" + 末梢血液像 +
                ", ＨｂＡ１ｃ=" + ＨｂＡ１ｃ +
                ", ＰＴ=" + ＰＴ +
                ", ＧＯＴ=" + ＧＯＴ +
                ", ＧＰＴ=" + ＧＰＴ +
                ", γＧＴＰ=" + γＧＴＰ +
                ", ＣＰＫ=" + ＣＰＫ +
                ", クレアチニン=" + クレアチニン +
                ", 尿酸=" + 尿酸 +
                ", ＬＤＬコレステロール=" + ＬＤＬコレステロール +
                ", ＨＤＬコレステロール=" + ＨＤＬコレステロール +
                ", ＴＧ=" + ＴＧ +
                ", グルコース=" + グルコース +
                ", カリウム=" + カリウム +
                ", ＴＳＨ=" + ＴＳＨ +
                ", ＦＴ４=" + ＦＴ４ +
                ", ＦＴ３=" + ＦＴ３ +
                ", ＰＳＡ=" + ＰＳＡ +
                ", 蛋白定量尿=" + 蛋白定量尿 +
                ", クレアチニン尿=" + クレアチニン尿 +
                ", ＣＲＰ=" + ＣＲＰ +
                ", 非特異的ＩｇＥ=" + 非特異的ＩｇＥ +
                '}';
    }
}
