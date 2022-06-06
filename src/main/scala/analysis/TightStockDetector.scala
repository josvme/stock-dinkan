package analysis

import models.DayData

object TightStockDetector extends AnalysisTrait {
  // stock open-close shouldn't oscillate more than 4% from centre.

  override def passAnalysis(
      index: Vector[DayData],
      stocks: Vector[DayData]
  ): Boolean = {
    val blankListCompanies = List(
      "AAC",
      "AACI",
      "AAIC",
      "AAQC",
      "AB",
      "ABCB",
      "ABGI",
      "ABR",
      "ABTX",
      "ACAB",
      "ACAH",
      "ACAQ",
      "ACAX",
      "ACBA",
      "ACBI",
      "ACDI",
      "ACEV",
      "ACII",
      "ACNB",
      "ACQR",
      "ACR",
      "ACRE",
      "ACRO",
      "ACT",
      "ACTD",
      "ACXP",
      "ADAL",
      "ADER",
      "ADEX",
      "ADGI",
      "ADOC",
      "ADRA",
      "ADRT",
      "AEAC",
      "AEAE",
      "AEHA",
      "AERC",
      "AFAC",
      "AFAQ",
      "AFBI",
      "AFCG",
      "AFTR",
      "AGAC",
      "AGBA",
      "AGCB",
      "AGGR",
      "AGM",
      "AGNC",
      "AHPA",
      "AHRN",
      "AIB",
      "AIKI",
      "AINV",
      "AJX",
      "AKIC",
      "AL",
      "ALAC",
      "ALCC",
      "ALLY",
      "ALOR",
      "ALPA",
      "ALRS",
      "ALSA",
      "ALTU",
      "ALZN",
      "AMAL",
      "AMAO",
      "AMCI",
      "AMLX",
      "AMNB",
      "AMPI",
      "AMTB",
      "AMTD",
      "ANAC",
      "ANEB",
      "ANIX",
      "ANVS",
      "ANZU",
      "AOGO",
      "AOMR",
      "APAC",
      "APCA",
      "APCX",
      "APGB",
      "APLT",
      "APMI",
      "APN",
      "APO",
      "APSG",
      "APTM",
      "APXI",
      "ARBG",
      "ARCC",
      "ARCK",
      "ARGU",
      "ARI",
      "ARIZ",
      "AROW",
      "ARR",
      "ARRW",
      "ARTA",
      "ARTE",
      "ARTL",
      "ARYD",
      "ARYE",
      "ASA",
      "ASAQ",
      "ASAX",
      "ASB",
      "ASPA",
      "ASPC",
      "ASRV",
      "ASZ",
      "ATA",
      "ATAK",
      "ATAQ",
      "ATAX",
      "ATEK",
      "ATLC",
      "ATLO",
      "ATVC",
      "ATXI",
      "ATXS",
      "AUB",
      "AUBN",
      "AURC",
      "AUS",
      "AVAC",
      "AVAL",
      "AVAN",
      "AVHI",
      "AVTE",
      "AVXL",
      "AX",
      "AXH",
      "AXP",
      "BAC",
      "BACA",
      "BAFN",
      "BANC",
      "BANF",
      "BANR",
      "BANX",
      "BAP",
      "BBAR",
      "BBD",
      "BBDC",
      "BBLG",
      "BBVA",
      "BCAC",
      "BCBP",
      "BCH",
      "BCML",
      "BCOW",
      "BCS",
      "BCSA",
      "BCSF",
      "BEAT",
      "BENE",
      "BFAC",
      "BFC",
      "BFIN",
      "BFST",
      "BGCP",
      "BGSX",
      "BHAC",
      "BHB",
      "BHF",
      "BHLB",
      "BHSE",
      "BIOS",
      "BIOT",
      "BITE",
      "BK",
      "BKCC",
      "BKSC",
      "BKU",
      "BLEU",
      "BLFY",
      "BLNG",
      "BLSA",
      "BLTS",
      "BLUA",
      "BLX",
      "BMA",
      "BMAC",
      "BMAQ",
      "BMO",
      "BMRC",
      "BNIX",
      "BNNR",
      "BNS",
      "BOAC",
      "BOAS",
      "BOCN",
      "BOH",
      "BOKF",
      "BOTJ",
      "BPAC",
      "BPOP",
      "BPRN",
      "BPT",
      "BRAC",
      "BRBS",
      "BRD",
      "BREZ",
      "BRIV",
      "BRKH",
      "BRKL",
      "BRLI",
      "BRMK",
      "BRPM",
      "BSAC",
      "BSAQ",
      "BSBK",
      "BSBR",
      "BSGA",
      "BSKY",
      "BSMX",
      "BSRR",
      "BSVN",
      "BTCY",
      "BTNB",
      "BTWN",
      "BUSE",
      "BVXV",
      "BWAC",
      "BWAQ",
      "BWB",
      "BWC",
      "BWFG",
      "BX",
      "BXMT",
      "BY",
      "BYFC",
      "BYN",
      "BYTS",
      "C",
      "CAC",
      "CACC",
      "CADE",
      "CALB",
      "CALT",
      "CARE",
      "CARV",
      "CAS",
      "CASH",
      "CATC",
      "CATY",
      "CBAN",
      "CBFV",
      "CBNK",
      "CBRG",
      "CBSH",
      "CBTX",
      "CBU",
      "CCAI",
      "CCAP",
      "CCB",
      "CCBG",
      "CCNE",
      "CCTS",
      "CCU",
      "CCV",
      "CCVI",
      "CDAQ",
      "CENN",
      "CENQ",
      "CFB",
      "CFBK",
      "CFFE",
      "CFFI",
      "CFFN",
      "CFFS",
      "CFG",
      "CFIV",
      "CFR",
      "CFSB",
      "CFVI",
      "CGBD",
      "CHAA",
      "CHCO",
      "CHMG",
      "CHMI",
      "CHPM",
      "CHWA",
      "CIB",
      "CIIG",
      "CIM",
      "CINC",
      "CION",
      "CITE",
      "CIVB",
      "CIZN",
      "CKPT",
      "CLAA",
      "CLAQ",
      "CLAS",
      "CLAY",
      "CLBK",
      "CLBR",
      "CLIM",
      "CLOE",
      "CLRM",
      "CLST",
      "CM",
      "CMA",
      "CMCA",
      "CMPI",
      "CMTG",
      "CND",
      "CNDA",
      "CNDB",
      "CNF",
      "CNGL",
      "CNNB",
      "CNOB",
      "CNTA",
      "CNTQ",
      "CNTX",
      "COF",
      "COFS",
      "COHN",
      "COLB",
      "COLI",
      "CONX",
      "COOL",
      "CORS",
      "COVA",
      "COWN",
      "CPAA",
      "CPAR",
      "CPF",
      "CPSS",
      "CPTK",
      "CPUH",
      "CREC",
      "CREG",
      "CRHC",
      "CRT",
      "CRU",
      "CRZN",
      "CS",
      "CSLM",
      "CSTA",
      "CSTR",
      "CTAQ",
      "CTBI",
      "CTGO",
      "CUBI",
      "CULL",
      "CVBF",
      "CVCY",
      "CVII",
      "CVLY",
      "CWBC",
      "CXAC",
      "CZNC",
      "CZWI",
      "DALS",
      "DAOO",
      "DATS",
      "DB",
      "DCOM",
      "DCRD",
      "DFS",
      "DGNU",
      "DHAC",
      "DHBC",
      "DHCA",
      "DHHC",
      "DILA",
      "DISA",
      "DKDCA",
      "DLCA",
      "DMAQ",
      "DMYS",
      "DNAA",
      "DNAB",
      "DNAC",
      "DNAD",
      "DNZ",
      "DPCS",
      "DRAY",
      "DRUG",
      "DSAC",
      "DSAQ",
      "DTOC",
      "DTRT",
      "DUET",
      "DUNE",
      "DWAC",
      "DWIN",
      "DX",
      "DXF",
      "DXR",
      "DYAI",
      "DYNS",
      "EAC",
      "EARN",
      "EBAC",
      "EBC",
      "EBET",
      "EBMT",
      "EBTC",
      "EC",
      "ECC",
      "EDNC",
      "EDTX",
      "EFC",
      "EFSC",
      "EGBN",
      "EGGF",
      "EIC",
      "EJFA",
      "ELMS",
      "ELYM",
      "EMCF",
      "EMLD",
      "ENCP",
      "ENER",
      "ENIC",
      "ENPC",
      "ENTF",
      "ENVA",
      "ENVB",
      "EOCW",
      "EPHY",
      "EPIX",
      "EPWR",
      "EQBK",
      "EQD",
      "EQHA",
      "EQS",
      "ERES",
      "ERIC",
      "ESAC",
      "ESBK",
      "ESM",
      "ESQ",
      "ESSA",
      "ESSC",
      "ETAC",
      "EUCR",
      "EVBN",
      "EVE",
      "EVOJ",
      "EVOK",
      "EVR",
      "EWBC",
      "FACA",
      "FACT",
      "FATP",
      "FBC",
      "FBIZ",
      "FBK",
      "FBMS",
      "FBNC",
      "FBP",
      "FBRT",
      "FCAP",
      "FCAX",
      "FCBC",
      "FCCO",
      "FCF",
      "FCNCA",
      "FCRD",
      "FDBC",
      "FDUS",
      "FENC",
      "FEXD",
      "FFBC",
      "FFBW",
      "FFIC",
      "FFIN",
      "FFNW",
      "FFWM",
      "FGBI",
      "FGF",
      "FHB",
      "FHLT",
      "FHN",
      "FIAC",
      "FIBK",
      "FICV",
      "FINM",
      "FINW",
      "FISI",
      "FITB",
      "FLAC",
      "FLAG",
      "FLIC",
      "FLME",
      "FLYA",
      "FMAC",
      "FMAO",
      "FMBH",
      "FMIV",
      "FMNB",
      "FNB",
      "FNCB",
      "FNLC",
      "FNVT",
      "FNWB",
      "FNWD",
      "FOA",
      "FOUN",
      "FOXW",
      "FPAC",
      "FRAF",
      "FRBA",
      "FRBK",
      "FRBN",
      "FRC",
      "FRHC",
      "FRLA",
      "FRME",
      "FRON",
      "FRSG",
      "FRST",
      "FRW",
      "FRXB",
      "FSBC",
      "FSBW",
      "FSEA",
      "FSFG",
      "FSK",
      "FSNB",
      "FSRX",
      "FSSI",
      "FST",
      "FTAA",
      "FTCV",
      "FTEV",
      "FTPA",
      "FTVI",
      "FULT",
      "FUNC",
      "FUSB",
      "FUTU",
      "FVAM",
      "FVCB",
      "FVIV",
      "FVT",
      "FWAC",
      "FWP",
      "FXCO",
      "FXNC",
      "FZT",
      "GABC",
      "GACQ",
      "GAIN",
      "GALT",
      "GAMC",
      "GAPA",
      "GAQ",
      "GATE",
      "GBCI",
      "GBDC",
      "GBNY",
      "GBRG",
      "GBS",
      "GCBC",
      "GDNR",
      "GDOT",
      "GECC",
      "GEEX",
      "GFED",
      "GFGD",
      "GFOR",
      "GFX",
      "GGAA",
      "GGAL",
      "GGGV",
      "GGMC",
      "GGPI",
      "GHAC",
      "GHIX",
      "GHL",
      "GIA",
      "GIAC",
      "GIIX",
      "GIW",
      "GLAD",
      "GLAQ",
      "GLBL",
      "GLBZ",
      "GLEE",
      "GLHA",
      "GLLI",
      "GLTA",
      "GLTO",
      "GMBT",
      "GMFI",
      "GNAC",
      "GNTY",
      "GOAC",
      "GOBI",
      "GOGN",
      "GPAC",
      "GPCO",
      "GPMT",
      "GRCY",
      "GRNA",
      "GS",
      "GSAQ",
      "GSBC",
      "GSBD",
      "GSEV",
      "GSQB",
      "GSQD",
      "GTAC",
      "GTBP",
      "GTPA",
      "GTPB",
      "GVCI",
      "GWII",
      "GXII",
      "HAAC",
      "HAFC",
      "HAIA",
      "HASI",
      "HBAN",
      "HBCP",
      "HBNC",
      "HBT",
      "HCAR",
      "HCIC",
      "HCII",
      "HCMA",
      "HCNE",
      "HCVI",
      "HDB",
      "HEPS",
      "HERA",
      "HFBL",
      "HFWA",
      "HGEN",
      "HHGC",
      "HHLA",
      "HIFS",
      "HIGA",
      "HIII",
      "HILS",
      "HLAH",
      "HLI",
      "HLXA",
      "HMA",
      "HMCO",
      "HMNF",
      "HMST",
      "HOMB",
      "HONE",
      "HOOD",
      "HOPE",
      "HORI",
      "HOUR",
      "HPLT",
      "HPX",
      "HRZN",
      "HSAQ",
      "HSBC",
      "HTAQ",
      "HTBI",
      "HTBK",
      "HTH",
      "HTLF",
      "HTOO",
      "HTPA",
      "HUGS",
      "HVBC",
      "HWBK",
      "HWC",
      "HWEL",
      "HWKZ",
      "HYAC",
      "HZON",
      "IACC",
      "IBCP",
      "IBER",
      "IBKR",
      "IBN",
      "IBOC",
      "IBTX",
      "ICMB",
      "ICNC",
      "ICVX",
      "IFIN",
      "IFS",
      "IGAC",
      "IGNY",
      "IGTA",
      "IIII",
      "IKT",
      "IMAQ",
      "IMH",
      "IMPX",
      "IMRN",
      "INAQ",
      "INBK",
      "INCR",
      "INDB",
      "INDI",
      "ING",
      "INKA",
      "INMB",
      "INTE",
      "IOAC",
      "IOBT",
      "IOR",
      "IPAX",
      "IPOD",
      "IPOF",
      "IPVA",
      "IPVF",
      "IPVI",
      "IQMD",
      "IROQ",
      "IRRX",
      "ISAA",
      "ISBC",
      "ISLE",
      "ISTR",
      "ITAQ",
      "ITCB",
      "ITHX",
      "ITQ",
      "ITUB",
      "IVCB",
      "IVCP",
      "IVR",
      "IXAQ",
      "JAQC",
      "JATT",
      "JCIC",
      "JEF",
      "JMAC",
      "JOFF",
      "JPM",
      "JUGG",
      "JUN",
      "JWAC",
      "JWSM",
      "JXN",
      "JYAC",
      "KACL",
      "KAHC",
      "KAII",
      "KAIR",
      "KAVL",
      "KB",
      "KCGI",
      "KEY",
      "KFFB",
      "KIII",
      "KINZ",
      "KKR",
      "KLAQ",
      "KNSW",
      "KREF",
      "KRNL",
      "KRNY",
      "KSI",
      "KTTA",
      "KVSA",
      "KVSC",
      "KWAC",
      "KXIN",
      "KYCH",
      "LAAA",
      "LADR",
      "LARK",
      "LATG",
      "LAX",
      "LAZ",
      "LBAI",
      "LBC",
      "LCA",
      "LCAA",
      "LCAP",
      "LCNB",
      "LCW",
      "LDHA",
      "LDI",
      "LEAP",
      "LEGA",
      "LOKM",
      "LEVL",
      "LFAC",
      "LFT",
      "LFTR",
      "LGAC",
      "LGHL",
      "LGST",
      "LGTO",
      "LGV",
      "LGVC",
      "LHAA",
      "LHC",
      "LIBY",
      "LION",
      "LITM",
      "LITT",
      "LIVB",
      "LIXT",
      "LJAQ",
      "LKFN",
      "LMACA",
      "LMAO",
      "LMFA",
      "LMST",
      "LNFA",
      "LOAN",
      "LOB",
      "LOCC",
      "LRFC",
      "LSBK",
      "LSPR",
      "LUXA",
      "LVAC",
      "LVRA",
      "LYG",
      "MAAQ",
      "MACA",
      "MACC",
      "MACK",
      "MACU",
      "MAIN",
      "MAQC",
      "MARPS",
      "MBAC",
      "MBCN",
      "MBI",
      "MBIN",
      "MBSC",
      "MBTC",
      "MBWM",
      "MC",
      "MCAA",
      "MCAE",
      "MCAF",
      "MCAG",
      "MCB",
      "MCBC",
      "MCBS",
      "MDH",
      "MEAC",
      "MEKA",
      "MEOA",
      "MFA",
      "MFG",
      "MFH",
      "MGYR",
      "MIT",
      "MITA",
      "MITT",
      "MLAC",
      "MLAI",
      "MLVF",
      "MNPR",
      "MNSB",
      "MNTN",
      "MOFG",
      "MOGO",
      "MON",
      "MOXC",
      "MPAC",
      "MPB",
      "MPRA",
      "MRBK",
      "MRCC",
      "MS",
      "MSAC",
      "MSB",
      "MSBI",
      "MSDA",
      "MSVB",
      "MTAC",
      "MTAL",
      "MTB",
      "MTR",
      "MTRY",
      "MTVC",
      "MUDS",
      "MUFG",
      "MURF",
      "MVBF",
      "MYFW",
      "MYNZ",
      "NAAC",
      "NAVI",
      "NBHC",
      "NBN",
      "NBST",
      "NBTB",
      "NCAC",
      "NCBS",
      "NDAC",
      "NECB",
      "NETC",
      "NFBK",
      "NFNT",
      "NFYS",
      "NGC",
      "NHIC",
      "NICK",
      "NKSH",
      "NLIT",
      "NLY",
      "NMFC",
      "NMMC",
      "NMR",
      "NNI",
      "NOAC",
      "NOVV",
      "NPAB",
      "NRAC",
      "NREF",
      "NRIM",
      "NRSN",
      "NRT",
      "NRZ",
      "NSTB",
      "NSTC",
      "NSTD",
      "NSTS",
      "NTB",
      "NTRS",
      "NU",
      "NUVL",
      "NVAC",
      "NVCT",
      "NVSA",
      "NWBI",
      "NWFL",
      "NWG",
      "NXU",
      "NYCB",
      "NYMT",
      "OACB",
      "OBNK",
      "OBT",
      "OCA",
      "OCAX",
      "OCCI",
      "OCFC",
      "OCSL",
      "OEPW",
      "OFED",
      "OFG",
      "OHAA",
      "OHPA",
      "OLIT",
      "OZK",
      "PACI",
      "PACW",
      "PACX",
      "PAFO",
      "OMEG",
      "OMF",
      "ONB",
      "ONCT",
      "ONYX",
      "OP",
      "OPA",
      "OPBK",
      "OPFI",
      "OPHC",
      "OPOF",
      "OPRT",
      "OPY",
      "ORC",
      "ORCC",
      "ORIA",
      "ORRF",
      "OSBC",
      "OSI",
      "OSTR",
      "OTEC",
      "OTRA",
      "OVBC",
      "OVLY",
      "OXAC",
      "OXSQ",
      "OXUS",
      "PANA",
      "PAQC",
      "PB",
      "PBAX",
      "PBBK",
      "PBCT",
      "PBFS",
      "PBHC",
      "PBIP",
      "PBLA",
      "PBT",
      "PCB",
      "PCCT",
      "PCPC",
      "PCSB",
      "PCX",
      "PDLB",
      "PDOT",
      "PEBK",
      "PEBO",
      "PEGR",
      "PEPL",
      "PFBC",
      "PFC",
      "PFDR",
      "PFHD",
      "PFIS",
      "PFLT",
      "PFS",
      "PFSI",
      "PFTA",
      "PFX",
      "PGC",
      "PGRW",
      "PGSS",
      "PHIC",
      "PHYT",
      "PIAI",
      "PICC",
      "PIPP",
      "PIPR",
      "PJT",
      "PKBK",
      "PLBC",
      "PLMI",
      "PMCB",
      "PMGM",
      "PMT",
      "PMVC",
      "PNBK",
      "PNC",
      "PNFP",
      "PNNT",
      "PNT",
      "PNTM",
      "POND",
      "PONO",
      "PORT",
      "POW",
      "PPBI",
      "PPGH",
      "PPHP",
      "PPYA",
      "PRBM",
      "PRDS",
      "PRK",
      "PRLH",
      "PROV",
      "PRPB",
      "PRPC",
      "PRSR",
      "PRT",
      "PRTG",
      "PSAG",
      "PSEC",
      "PSPC",
      "PSTH",
      "PTIC",
      "PTMN",
      "PTNR",
      "PTOC",
      "PTRS",
      "PUCK",
      "PV",
      "PVBC",
      "PVL",
      "PWOD",
      "PWP",
      "QCRH",
      "QFTA",
      "RACB",
      "RAM",
      "RBAC",
      "RBB",
      "RBCAA",
      "RBKB",
      "RC",
      "RCAT",
      "RCFA",
      "RCHG",
      "RCLF",
      "RE",
      "REFI",
      "REVB",
      "REVE",
      "REVH",
      "RF",
      "RICO",
      "RIDE",
      "RILY",
      "RJF",
      "RKTA",
      "RMBI",
      "RMGC",
      "RNAZ",
      "RNDB",
      "RNER",
      "RNST",
      "RNXT",
      "ROC",
      "ROCG",
      "ROCR",
      "ROIV",
      "RONI",
      "ROSE",
      "ROSS",
      "RRAC",
      "RRBI",
      "RVAC",
      "RVSB",
      "RWAY",
      "RWT",
      "RXRA",
      "RY",
      "SACH",
      "SAGA",
      "SAL",
      "SAMA",
      "SAN",
      "SANB",
      "SAR",
      "SASR",
      "SBCF",
      "SBEA",
      "SBFG",
      "SBII",
      "SBNY",
      "SBR",
      "SBSI",
      "SBT",
      "SCAQ",
      "SCHW",
      "SCLE",
      "SCM",
      "SCMA",
      "SCOA",
      "SCOB",
      "SCUA",
      "SCVX",
      "SDAC",
      "SEDA",
      "SEEL",
      "SEVN",
      "SF",
      "SFBC",
      "SFBS",
      "SFE",
      "SFNC",
      "SFST",
      "SGII",
      "SIVB",
      "SHAC",
      "SHBI",
      "SHCA",
      "SHG",
      "SHQA",
      "SI",
      "SIEB",
      "SIER",
      "SJT",
      "SKYA",
      "SKYH",
      "SLAC",
      "SLAM",
      "SLCR",
      "SLM",
      "SLRC",
      "SLS",
      "SLVR",
      "SMAP",
      "SMBC",
      "SMBK",
      "SMFG",
      "SMIH",
      "SMMF",
      "SNAX",
      "SNEX",
      "SNII",
      "SNRH",
      "SNV",
      "SOFI",
      "SPAQ",
      "SPFI",
      "SPGS",
      "SPK",
      "SPKB",
      "SPNT",
      "SPTK",
      "SRCE",
      "SRL",
      "SRSA",
      "SSAA",
      "SSB",
      "SSBI",
      "SSBK",
      "SSIC",
      "SSSS",
      "STAB",
      "STBA",
      "STRE",
      "STT",
      "STWD",
      "STXB",
      "SUAC",
      "SUNS",
      "SUPV",
      "SV",
      "SVFA",
      "SVFB",
      "SVFC",
      "SVNA",
      "SVVC",
      "SWAG",
      "SWET",
      "SWKH",
      "SWSS",
      "SYBT",
      "SYF",
      "SZZL",
      "TACA",
      "TBBK",
      "TBCP",
      "TBK",
      "TBLD",
      "TBNK",
      "TBSA",
      "TCAC",
      "TCBC",
      "TCBI",
      "TCBK",
      "TCBS",
      "TCBX",
      "TCFC",
      "TRMK",
      "TRST",
      "TSBK",
      "TSIB",
      "TSPQ",
      "TCPC",
      "TCVA",
      "TD",
      "TEKK",
      "TETC",
      "TFC",
      "TFFP",
      "TFSL",
      "TGAA",
      "TRTX",
      "TSC",
      "TGVC",
      "THAC",
      "THCA",
      "THCP",
      "THFF",
      "TIGR",
      "TINV",
      "TIOA",
      "TKC",
      "TLGA",
      "TRON",
      "TRTL",
      "TSLX",
      "TUGC",
      "TVAC",
      "TW",
      "TWCB",
      "TLK",
      "TMAC",
      "TMBR",
      "TMKR",
      "TMP",
      "TMPM",
      "TOAC",
      "TOWN",
      "TPBA",
      "TPGY",
      "TPVG",
      "TRAQ",
      "TRCA",
      "TRIS",
      "TWLV",
      "TWND",
      "TWNI",
      "TWNT",
      "TWO",
      "TWOA",
      "TYG",
      "TZPS",
      "UBCP",
      "UBFO",
      "UBOH",
      "UBS",
      "UBSI",
      "UCBI",
      "UMBF",
      "UMPQ",
      "UNB",
      "UNCY",
      "UNTY",
      "UPH",
      "UPTD",
      "UROY",
      "USB",
      "USCB",
      "USCT",
      "VRPX",
      "UVSP",
      "VABK",
      "VAQC",
      "VBFC",
      "VBTX",
      "VCKA",
      "VCXA",
      "VEL",
      "VELO",
      "VENA",
      "VGII",
      "VHAQ",
      "VHNA",
      "VIGL",
      "VII",
      "VINC",
      "VIRI",
      "VIRT",
      "VKTX",
      "VLAT",
      "VLY",
      "VMGA",
      "VOC",
      "VPCB",
      "VSAC",
      "VTAQ",
      "VTIQ",
      "VTYX",
      "VYGG",
      "WABC",
      "WAFD",
      "WAL",
      "WALD",
      "WARR",
      "WASH",
      "WAVC",
      "WAVE",
      "WBS",
      "WD",
      "WEL",
      "WF",
      "WFC",
      "WHF",
      "WILC",
      "WINV",
      "WMC",
      "WMPN",
      "WNEB",
      "WPCA",
      "WPCB",
      "WQGA",
      "WRAC",
      "WRN",
      "WSBC",
      "WSBF",
      "WSFS",
      "WTBA",
      "WTFC",
      "WTMA",
      "WULF",
      "WVFC",
      "WWAC",
      "XFIN",
      "XP",
      "XPAX",
      "XPOA",
      "XTLB",
      "YTPG",
      "ZING",
      "ZION",
      "ZIVO",
      "ZNTE",
      "ZT",
      "ZWRK"
    )
    val isABlankCheckCompany =
      stocks.headOption.forall(d => blankListCompanies.contains(d.symbol))

    if (isABlankCheckCompany) {
      return false;
    }

    // Tight action for 5 days
    val last5Days = stocks.reverse.take(5)
    val lowestLowPoint =
      last5Days.reduce((x, y) => if (x.low < y.low) x else y)
    val highestHighPoint =
      last5Days.reduce((x, y) => if (x.high > y.high) x else y)
    val middlePriceForLast10Days =
      last5Days.foldRight(0.0)((x, e) => x.low + x.high + e) / 2 / 5

    val diffHighLow = highestHighPoint.high - lowestLowPoint.low
    // Approximately 3*2 => 6%
    diffHighLow < middlePriceForLast10Days * .03
  }

  override def name(): String = "Tight Stock Analyzer"
}
