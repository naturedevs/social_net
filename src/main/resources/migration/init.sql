CREATE DATABASE IF NOT EXISTS radius1;

USE radius1;

CREATE TABLE FILE
(
    id INT AUTO_INCREMENT
        PRIMARY KEY,
    created_at DATETIME(6) NULL,
    description VARCHAR(255) NULL,
    title VARCHAR(255) NULL,
    updated_at DATETIME(6) NULL,
    url VARCHAR(255) NULL
);

CREATE TABLE gateway
(
    id INT AUTO_INCREMENT
        PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL
);

CREATE TABLE geolocation
(
    id INT NOT NULL
        PRIMARY KEY,
    CODE VARCHAR(255) NULL,
    NAME VARCHAR(255) NULL,
    three_char_code VARCHAR(255) NULL
);

create table news
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    description varchar(255) null,
    send_mail bit null,
    title varchar(255) null,
    updated_at datetime(6) null
);

create table reseller_level
(
    id int not null
        primary key,
    discount_percent decimal(19,2) null,
    min_score decimal(19,2) null,
    name varchar(255) null
);

create table reseller
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    credit decimal(19,2) null,
    enabled bit null,
    level_set_date datetime(6) null,
    phone varchar(255) null,
    updated_at datetime(6) null,
    level_id int null,
    user_id int null,
    constraint FK39t9mqgxa2np88nh4ath5pib3
        foreign key (level_id) references reseller_level (id)
);

create table reseller_add_credit
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    credit decimal(19,2) null,
    reseller_id int null,
    constraint FKiqtox4qkr3f73mc7rjjwmqube
        foreign key (reseller_id) references reseller (id)
);

create table reseller_level_coefficients
(
    id int not null
        primary key,
    active_subscription_max decimal(19,2) null,
    active_subscription_percent decimal(19,2) null,
    current_credit_max decimal(19,2) null,
    current_credit_percent decimal(19,2) null,
    deposit_interval_manual_days decimal(19,2) null,
    deposit_interval_max decimal(19,2) null,
    membership_duration_max decimal(19,2) null,
    membership_duration_percent decimal(19,2) null,
    month_credit_max decimal(19,2) null,
    month_credit_percent decimal(19,2) null,
    month_sale_max decimal(19,2) null,
    month_sale_percent decimal(19,2) null,
    total_sale_max decimal(19,2) null,
    total_sale_percent decimal(19,2) null
);

create table role
(
    id int not null
        primary key,
    name varchar(255) null
);

create table file_roles
(
    file_id int not null,
    roles_id int not null,
    constraint FK3r4pxwbt10tgv1i44byccxmfa
        foreign key (file_id) references file (id),
    constraint FKorq7cg6v7x93a24crnhgp7jie
        foreign key (roles_id) references role (id)
);

create table news_roles
(
    news_id int not null,
    roles_id int not null,
    constraint FK7rnc4was693yp0n7yq2cii5uo
        foreign key (news_id) references news (id),
    constraint FKrqoe6ok05ewg1cisb2fvbjuus
        foreign key (roles_id) references role (id)
);

create table server
(
    id int auto_increment
        primary key,
    city varchar(255) null,
    country varchar(255) null,
    created_at datetime(6) null,
    description varchar(255) null,
    host_name varchar(255) null,
    kill_command varchar(255) null,
    ports int null,
    private_ip varchar(255) null,
    public_ip varchar(255) null,
    root_command varchar(255) null,
    secret varchar(255) null,
    ssh_key varchar(255) null,
    ssh_username varchar(255) null,
    type varchar(255) null,
    updated_at datetime(6) null,
    ssh_private_key varchar(2000) null,
    constraint UK_82wg05oxphw77cf2dv2pn1omp
        unique (public_ip)
);

create table service_group
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    description varchar(255) not null,
    discount decimal(19,2) null,
    language varchar(255) null,
    name varchar(255) not null,
    deleted bit DEFAULT 0,
    updated_at datetime(6) null
);

create table group_app
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    daily_bandwidth decimal(19,2) null,
    description varchar(255) not null,
    download_upload decimal(19,2) null,
    duration int null,
    ip varchar(255) null,
    multi_login_count int null,
    name varchar(255) not null,
    price decimal(19,2) null,
    tag_name varchar(255) not null,
    updated_at datetime(6) null,
    username_postfix varchar(255) null,
    username_postfix_id varchar(255) null,
    service_group_id int null,
    deleted bit DEFAULT 0,
    registration_group bit DEFAULT 0,
    constraint FKtouoho0ujtluniystqjyoauoc
        foreign key (service_group_id) references service_group (id)
);

create table reseller_service_groups
(
    reseller_id int not null,
    service_groups_id int not null,
    primary key (reseller_id, service_groups_id),
    constraint FK2qkptr3kdgvyeohi24s5nyvsq
        foreign key (service_groups_id) references service_group (id),
    constraint FKjyq21j66v1uhf5l7rnq2qp304
        foreign key (reseller_id) references reseller (id)
);

create table service_group_allowed_geolocations
(
    service_group_id int not null,
    allowed_geolocations_id int not null,
    constraint FK6ur8drku12byhh1lhu0h5umke
        foreign key (service_group_id) references service_group (id),
    constraint FKthqr8o7r3tiaw1q7riiqt9t8l
        foreign key (allowed_geolocations_id) references geolocation (id)
);

create table service_group_dis_allowed_geolocations
(
    service_group_id int not null,
    dis_allowed_geolocations_id int not null,
    constraint FKmqb3ew7bu3tg5b9t39jhfhncj
        foreign key (dis_allowed_geolocations_id) references geolocation (id),
    constraint FKnuf6s75gi7seqw2b2ps97xhun
        foreign key (service_group_id) references service_group (id)
);

create table service_group_gateways
(
    service_group_id int not null,
    gateways_id int not null,
    constraint FK4oyr1o6684bdv9jhmiq739489
        foreign key (service_group_id) references service_group (id),
    constraint FKddwsiexlbjawslgl5cchdoskg
        foreign key (gateways_id) references gateway (id)
);

create table user
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    email varchar(255) not null,
    enabled bit null,
    password varchar(255) null,
    rad_access varchar(255) not null,
    rad_access_clear varchar(255) null,
    updated_at datetime(6) null,
    username varchar(255) not null,
    reseller_id int null,
    role_id int null,
    constraint FKn82ha3ccdebhokx3a8fgdqeyy
        foreign key (role_id) references role (id),
    constraint FKnw0i6ug7ho5getfmboajqvlry
        foreign key (reseller_id) references reseller (id)
);

create table oauth_token
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    exp bigint null,
    iat bigint null,
    social_media varchar(255) null,
    social_token varchar(255) null,
    token varchar(255) null,
    updated_at datetime(6) null,
    user_id int null,
    constraint FK2y78f55u617km0eu82gnqr7ux
        foreign key (user_id) references user (id)
);

create table password_reset
(
    token varchar(255) not null
        primary key,
    created_at datetime(6) null,
    user_id int null,
    constraint FK3rcc5avyc21uriav34cjrqc91
        foreign key (user_id) references user (id)
);

alter table reseller
    add constraint FK149bhi3y1jflv6dw60urqdlv9
        foreign key (user_id) references user (id);

create table ticket
(
    id int auto_increment
        primary key,
    category varchar(255) not null,
    created_at datetime(6) null,
    status varchar(255) not null,
    subject varchar(255) not null,
    text text not null,
    updated_at datetime(6) null,
    creator_id int null,
    constraint FKqwrtm8gdsjpjppg1ir9ylwaon
        foreign key (creator_id) references user (id)
);

create table ticket_reply
(
    id int auto_increment
        primary key,
    created_at datetime(6) null,
    text text null,
    updated_at datetime(6) null,
    creator_id int null,
    ticket_id int not null,
    constraint FKa0fc1iaijpkvps7lbmwxxr4hb
        foreign key (creator_id) references user (id),
    constraint FKsiaefs29fsoww8skxpo0g7ddh
        foreign key (ticket_id) references ticket (id)
);

create table user_profile
(
    id int auto_increment
        primary key,
    address varchar(255) null,
    city varchar(255) null,
    country varchar(255) null,
    created_at datetime(6) null,
    first_name varchar(255) null,
    last_name varchar(255) null,
    phone varchar(255) null,
    postal_code varchar(255) null,
    updated_at datetime(6) null,
    user_id int null,
    birth_date date null,
    constraint FK6kwj5lk78pnhwor4pgosvb51r
        foreign key (user_id) references user (id)
);

create table radius.user_subscription
(
    id                int auto_increment
        primary key,
    created_at        datetime(6)    null,
    daily_bandwidth   decimal(19, 2) null,
    download_upload   decimal(19, 2) null,
    duration          int            null,
    expires_at        datetime(6)    null,
    multi_login_count int            null,
    price             decimal(19, 2) null,
    updated_at        datetime(6)    null,
    group_id          int            null,
    user_id           int            null,
    renew             bit            null,
    renewed           bit            null,
    payment_id        int            null,
    constraint FKpsiiu2nyr0cbxeluuouw474s9
        foreign key (user_id) references radius.user (id),
    constraint FKt4tua4acgi5d647mxbklr9a46
        foreign key (group_id) references radius.group_app (id)
);



create table stripe_customer
(
    id        int auto_increment
        primary key,
    stripe_id varchar(255) null,
    user_id   int          null,
    constraint FK50iy5vrvy8g7u0nvbku22d6i5
        foreign key (user_id) references radius.user (id)
);

create table radius.payment
(
    id                int auto_increment
        primary key,
    category          varchar(255)   not null,
    created_at        datetime(6)    null,
    expires_at        datetime(6)    null,
    gateway           varchar(255)   not null,
    group_id          int            null,
    more_login_count int            null,
    payment_id        varchar(255)   null,
    price             decimal(19, 2) null,
    renew             bit            null,
    renewed           bit            null,
    status            varchar(255)   not null,
    updated_at        datetime(6)    null,
    user_id           int            null,
    meta_data         longtext       null
);

create table radius.apple_receipt
(
    id             int auto_increment
        primary key,
    created_at     datetime(6)  null,
    payment_status varchar(255) null,
    receipt        text         null,
    updated_at     datetime(6)  null,
    user_id        int          not null
);


CREATE TABLE IF NOT EXISTS radacct (
    radacctid bigint(21) NOT NULL auto_increment,
    acctsessionid varchar(64) NOT NULL default '',
    acctuniqueid varchar(32) NOT NULL default '',
    username varchar(64) NOT NULL default '',
    realm varchar(64) default '',
    nasipaddress varchar(15) NOT NULL default '',
    nasportid varchar(15) default NULL,
    nasporttype varchar(32) default NULL,
    acctstarttime datetime NULL default NULL,
    acctupdatetime datetime NULL default NULL,
    acctstoptime datetime NULL default NULL,
    acctinterval int(12) default NULL,
    acctsessiontime int(12) unsigned default NULL,
    acctauthentic varchar(32) default NULL,
    connectinfo_start varchar(50) default NULL,
    connectinfo_stop varchar(50) default NULL,
    acctinputoctets bigint(20) default NULL,
    acctoutputoctets bigint(20) default NULL,
    calledstationid varchar(50) NOT NULL default '',
    callingstationid varchar(50) NOT NULL default '',
    acctterminatecause varchar(32) NOT NULL default '',
    servicetype varchar(32) default NULL,
    framedprotocol varchar(32) default NULL,
    framedipaddress varchar(15) NOT NULL default '',
    framedipv6address varchar(45) NOT NULL default '',
    framedipv6prefix varchar(45) NOT NULL default '',
    framedinterfaceid varchar(44) NOT NULL default '',
    delegatedipv6prefix varchar(45) NOT NULL default '',
    PRIMARY KEY (radacctid),
    UNIQUE KEY acctuniqueid (acctuniqueid),
    KEY username (username),
    KEY framedipaddress (framedipaddress),
    KEY framedipv6address (framedipv6address),
    KEY framedipv6prefix (framedipv6prefix),
    KEY framedinterfaceid (framedinterfaceid),
    KEY delegatedipv6prefix (delegatedipv6prefix),
    KEY acctsessionid (acctsessionid),
    KEY acctsessiontime (acctsessiontime),
    KEY acctstarttime (acctstarttime),
    KEY acctinterval (acctinterval),
    KEY acctstoptime (acctstoptime),
    KEY nasipaddress (nasipaddress)
    ) ENGINE = INNODB;


#
# Table structure for table 'radcheck'
#

CREATE TABLE IF NOT EXISTS radcheck (
    id int(11) unsigned NOT NULL auto_increment,
    username varchar(64) NOT NULL default '',
    attribute varchar(64)  NOT NULL default '',
    op char(2) NOT NULL DEFAULT '==',
    value varchar(253) NOT NULL default '',
    PRIMARY KEY  (id),
    KEY username (username(32))
    );

#
# Table structure for table 'radgroupcheck'
#

CREATE TABLE IF NOT EXISTS radgroupcheck (
    id int(11) unsigned NOT NULL auto_increment,
    groupname varchar(64) NOT NULL default '',
    attribute varchar(64)  NOT NULL default '',
    op char(2) NOT NULL DEFAULT '==',
    value varchar(253)  NOT NULL default '',
    PRIMARY KEY  (id),
    KEY groupname (groupname(32))
    );

#
# Table structure for table 'radgroupreply'
#

CREATE TABLE IF NOT EXISTS radgroupreply (
    id int(11) unsigned NOT NULL auto_increment,
    groupname varchar(64) NOT NULL default '',
    attribute varchar(64)  NOT NULL default '',
    op char(2) NOT NULL DEFAULT '=',
    value varchar(253)  NOT NULL default '',
    PRIMARY KEY  (id),
    KEY groupname (groupname(32))
    );

#
# Table structure for table 'radreply'
#

CREATE TABLE IF NOT EXISTS radreply (
    id int(11) unsigned NOT NULL auto_increment,
    username varchar(64) NOT NULL default '',
    attribute varchar(64) NOT NULL default '',
    op char(2) NOT NULL DEFAULT '=',
    value varchar(253) NOT NULL default '',
    PRIMARY KEY  (id),
    KEY username (username(32))
    );

#
# Table structure for table 'radusergroup'
#

CREATE TABLE IF NOT EXISTS radusergroup (
    id int(11) unsigned NOT NULL auto_increment,
    username varchar(64) NOT NULL default '',
    groupname varchar(64) NOT NULL default '',
    priority int(11) NOT NULL default '1',
    PRIMARY KEY  (id),
    KEY username (username(32))
    );

#
# Table structure for table 'radpostauth'
#
CREATE TABLE IF NOT EXISTS radpostauth (
    id int(11) NOT NULL auto_increment,
    username varchar(64) NOT NULL default '',
    pass varchar(64) NOT NULL default '',
    reply varchar(32) NOT NULL default '',
    authdate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY  (id),
    KEY username (username(32))
    ) ENGINE = INNODB;

#
# Table structure for table 'nas'
#
CREATE TABLE IF NOT EXISTS nas (
    id int(10) NOT NULL auto_increment,
    nasname varchar(128) NOT NULL,
    shortname varchar(32),
    type varchar(30) DEFAULT 'other',
    ports int(5),
    secret varchar(60) DEFAULT 'secret' NOT NULL,
    server varchar(64),
    community varchar(50),
    description varchar(200) DEFAULT 'RADIUS Client',
    PRIMARY KEY (id),
    KEY nasname (nasname)
    );


INSERT INTO geolocation VALUES
(1,'Afghanistan','AF','AFG')
                             ,(2,'Aland Islands','AX','ALA')
                             ,(3,'Albania','AL','ALB')
                             ,(4,'Algeria','DZ','DZA')
                             ,(5,'American Samoa','AS','ASM')
                             ,(6,'Andorra','AD','AND')
                             ,(7,'Angola','AO','AGO')
                             ,(8,'Anguilla','AI','AIA')
                             ,(9,'Antarctica','AQ','ATA')
                             ,(10,'Antigua and Barbuda','AG','ATG')
                             ,(11,'Argentina','AR','ARG')
                             ,(12,'Armenia','AM','ARM')
                             ,(13,'Aruba','AW','ABW')
                             ,(14,'Australia','AU','AUS')
                             ,(15,'Austria','AT','AUT')
                             ,(16,'Azerbaijan','AZ','AZE')
                             ,(17,'Bahamas','BS','BHS')
                             ,(18,'Bahrain','BH','BHR')
                             ,(19,'Bangladesh','BD','BGD')
                             ,(20,'Barbados','BB','BRB')
                             ,(21,'Belarus','BY','BLR')
                             ,(22,'Belgium','BE','BEL')
                             ,(23,'Belize','BZ','BLZ')
                             ,(24,'Benin','BJ','BEN')
                             ,(25,'Bermuda','BM','BMU')
                             ,(26,'Bhutan','BT','BTN')
                             ,(27,'Bolivia','BO','BOL')
                             ,(28,'Bonaire, Sint Eustatius and Saba','BQ','BES')
                             ,(29,'Bosnia and Herzegovina','BA','BIH')
                             ,(30,'Botswana','BW','BWA')
                             ,(31,'Bouvet Island','BV','BVT')
                             ,(32,'Brazil','BR','BRA')
                             ,(33,'British Indian Ocean Territory','IO','IOT')
                             ,(34,'Brunei','BN','BRN')
                             ,(35,'Bulgaria','BG','BGR')
                             ,(36,'Burkina Faso','BF','BFA')
                             ,(37,'Burundi','BI','BDI')
                             ,(38,'Cambodia','KH','KHM')
                             ,(39,'Cameroon','CM','CMR')
                             ,(40,'Canada','CA','CAN')
                             ,(41,'Cape Verde','CV','CPV')
                             ,(42,'Cayman Islands','KY','CYM')
                             ,(43,'Central African Republic','CF','CAF')
                             ,(44,'Chad','TD','TCD')
                             ,(45,'Chile','CL','CHL')
                             ,(46,'China','CN','CHN')
                             ,(47,'Christmas Island','CX','CXR')
                             ,(48,'Cocos (Keeling) Islands','CC','CCK')
                             ,(49,'Colombia','CO','COL')
                             ,(50,'Comoros','KM','COM')
                             ,(51,'Congo','CG','COG')
                             ,(52,'Cook Islands','CK','COK')
                             ,(53,'Costa Rica','CR','CRI')
                             ,(54,'Ivory Coast','CI','CIV')
                             ,(55,'Croatia','HR','HRV')
                             ,(56,'Cuba','CU','CUB')
                             ,(57,'Curacao','CW','CUW')
                             ,(58,'Cyprus','CY','CYP')
                             ,(59,'Czech Republic','CZ','CZE')
                             ,(60,'Democratic Republic of the Congo','CD','COD')
                             ,(61,'Denmark','DK','DNK')
                             ,(62,'Djibouti','DJ','DJI')
                             ,(63,'Dominica','DM','DMA')
                             ,(64,'Dominican Republic','DO','DOM')
                             ,(65,'Ecuador','EC','ECU')
                             ,(66,'Egypt','EG','EGY')
                             ,(67,'El Salvador','SV','SLV')
                             ,(68,'Equatorial Guinea','GQ','GNQ')
                             ,(69,'Eritrea','ER','ERI')
                             ,(70,'Estonia','EE','EST')
                             ,(71,'Ethiopia','ET','ETH')
                             ,(72,'Falkland Islands (Malvinas)','FK','FLK')
                             ,(73,'Faroe Islands','FO','FRO')
                             ,(74,'Fiji','FJ','FJI')
                             ,(75,'Finland','FI','FIN')
                             ,(76,'France','FR','FRA')
                             ,(77,'French Guiana','GF','GUF')
                             ,(78,'French Polynesia','PF','PYF')
                             ,(79,'French Southern Territories','TF','ATF')
                             ,(80,'Gabon','GA','GAB')
                             ,(81,'Gambia','GM','GMB')
                             ,(82,'Georgia','GE','GEO')
                             ,(83,'Germany','DE','DEU')
                             ,(84,'Ghana','GH','GHA')
                             ,(85,'Gibraltar','GI','GIB')
                             ,(86,'Greece','GR','GRC')
                             ,(87,'Greenland','GL','GRL')
                             ,(88,'Grenada','GD','GRD')
                             ,(89,'Guadaloupe','GP','GLP')
                             ,(90,'Guam','GU','GUM')
                             ,(91,'Guatemala','GT','GTM')
                             ,(92,'Guernsey','GG','GGY')
                             ,(93,'Guinea','GN','GIN')
                             ,(94,'Guinea-Bissau','GW','GNB')
                             ,(95,'Guyana','GY','GUY')
                             ,(96,'Haiti','HT','HTI')
                             ,(97,'Heard Island and McDonald Islands','HM','HMD')
                             ,(98,'Honduras','HN','HND')
                             ,(99,'Hong Kong','HK','HKG')
                             ,(100,'Hungary','HU','HUN')
                             ,(101,'Iceland','IS','ISL')
                             ,(102,'India','IN','IND')
                             ,(103,'Indonesia','ID','IDN')
                             ,(104,'Iran','IR','IRN')
                             ,(105,'Iraq','IQ','IRQ')
                             ,(106,'Ireland','IE','IRL')
                             ,(107,'Isle of Man','IM','IMN')
                             ,(108,'Israel','IL','ISR')
                             ,(109,'Italy','IT','ITA')
                             ,(110,'Jamaica','JM','JAM')
                             ,(111,'Japan','JP','JPN')
                             ,(112,'Jersey','JE','JEY')
                             ,(113,'Jordan','JO','JOR')
                             ,(114,'Kazakhstan','KZ','KAZ')
                             ,(115,'Kenya','KE','KEN')
                             ,(116,'Kiribati','KI','KIR')
                             ,(117,'Kosovo','XK','---')
                             ,(118,'Kuwait','KW','KWT')
                             ,(119,'Kyrgyzstan','KG','KGZ')
                             ,(120,'Laos','LA','LAO')
                             ,(121,'Latvia','LV','LVA')
                             ,(122,'Lebanon','LB','LBN')
                             ,(123,'Lesotho','LS','LSO')
                             ,(124,'Liberia','LR','LBR')
                             ,(125,'Libya','LY','LBY')
                             ,(126,'Liechtenstein','LI','LIE')
                             ,(127,'Lithuania','LT','LTU')
                             ,(128,'Luxembourg','LU','LUX')
                             ,(129,'Macao','MO','MAC')
                             ,(130,'Macedonia','MK','MKD')
                             ,(131,'Madagascar','MG','MDG')
                             ,(132,'Malawi','MW','MWI')
                             ,(133,'Malaysia','MY','MYS')
                             ,(134,'Maldives','MV','MDV')
                             ,(135,'Mali','ML','MLI')
                             ,(136,'Malta','MT','MLT')
                             ,(137,'Marshall Islands','MH','MHL')
                             ,(138,'Martinique','MQ','MTQ')
                             ,(139,'Mauritania','MR','MRT')
                             ,(140,'Mauritius','MU','MUS')
                             ,(141,'Mayotte','YT','MYT')
                             ,(142,'Mexico','MX','MEX')
                             ,(143,'Micronesia','FM','FSM')
                             ,(144,'Moldava','MD','MDA')
                             ,(145,'Monaco','MC','MCO')
                             ,(146,'Mongolia','MN','MNG')
                             ,(147,'Montenegro','ME','MNE')
                             ,(148,'Montserrat','MS','MSR')
                             ,(149,'Morocco','MA','MAR')
                             ,(150,'Mozambique','MZ','MOZ')
                             ,(151,'Myanmar (Burma)','MM','MMR')
                             ,(152,'Namibia','NA','NAM')
                             ,(153,'Nauru','NR','NRU')
                             ,(154,'Nepal','NP','NPL')
                             ,(155,'Netherlands','NL','NLD')
                             ,(156,'New Caledonia','NC','NCL')
                             ,(157,'New Zealand','NZ','NZL')
                             ,(158,'Nicaragua','NI','NIC')
                             ,(159,'Niger','NE','NER')
                             ,(160,'Nigeria','NG','NGA')
                             ,(161,'Niue','NU','NIU')
                             ,(162,'Norfolk Island','NF','NFK')
                             ,(163,'North Korea','KP','PRK')
                             ,(164,'Northern Mariana Islands','MP','MNP')
                             ,(165,'Norway','NO','NOR')
                             ,(166,'Oman','OM','OMN')
                             ,(167,'Pakistan','PK','PAK')
                             ,(168,'Palau','PW','PLW')
                             ,(169,'Palestine','PS','PSE')
                             ,(170,'Panama','PA','PAN')
                             ,(171,'Papua New Guinea','PG','PNG')
                             ,(172,'Paraguay','PY','PRY')
                             ,(173,'Peru','PE','PER')
                             ,(174,'Phillipines','PH','PHL')
                             ,(175,'Pitcairn','PN','PCN')
                             ,(176,'Poland','PL','POL')
                             ,(177,'Portugal','PT','PRT')
                             ,(178,'Puerto Rico','PR','PRI')
                             ,(179,'Qatar','QA','QAT')
                             ,(180,'Reunion','RE','REU')
                             ,(181,'Romania','RO','ROU')
                             ,(182,'Russia','RU','RUS')
                             ,(183,'Rwanda','RW','RWA')
                             ,(184,'Saint Barthelemy','BL','BLM')
                             ,(185,'Saint Helena','SH','SHN')
                             ,(186,'Saint Kitts and Nevis','KN','KNA')
                             ,(187,'Saint Lucia','LC','LCA')
                             ,(188,'Saint Martin','MF','MAF')
                             ,(189,'Saint Pierre and Miquelon','PM','SPM')
                             ,(190,'Saint Vincent and the Grenadines','VC','VCT')
                             ,(191,'Samoa','WS','WSM')
                             ,(192,'San Marino','SM','SMR')
                             ,(193,'Sao Tome and Principe','ST','STP')
                             ,(194,'Saudi Arabia','SA','SAU')
                             ,(195,'Senegal','SN','SEN')
                             ,(196,'Serbia','RS','SRB')
                             ,(197,'Seychelles','SC','SYC')
                             ,(198,'Sierra Leone','SL','SLE')
                             ,(199,'Singapore','SG','SGP')
                             ,(200,'Sint Maarten','SX','SXM')
                             ,(201,'Slovakia','SK','SVK')
                             ,(202,'Slovenia','SI','SVN')
                             ,(203,'Solomon Islands','SB','SLB')
                             ,(204,'Somalia','SO','SOM')
                             ,(205,'South Africa','ZA','ZAF')
                             ,(206,'South Georgia and the South Sandwich Islands','GS','SGS')
                             ,(207,'South Korea','KR','KOR')
                             ,(208,'South Sudan','SS','SSD')
                             ,(209,'Spain','ES','ESP')
                             ,(210,'Sri Lanka','LK','LKA')
                             ,(211,'Sudan','SD','SDN')
                             ,(212,'Suriname','SR','SUR')
                             ,(213,'Svalbard and Jan Mayen','SJ','SJM')
                             ,(214,'Swaziland','SZ','SWZ')
                             ,(215,'Sweden','SE','SWE')
                             ,(216,'Switzerland','CH','CHE')
                             ,(217,'Syria','SY','SYR')
                             ,(218,'Taiwan','TW','TWN')
                             ,(219,'Tajikistan','TJ','TJK')
                             ,(220,'Tanzania','TZ','TZA')
                             ,(221,'Thailand','TH','THA')
                             ,(222,'Timor-Leste (East Timor)','TL','TLS')
                             ,(223,'Togo','TG','TGO')
                             ,(224,'Tokelau','TK','TKL')
                             ,(225,'Tonga','TO','TON')
                             ,(226,'Trinidad and Tobago','TT','TTO')
                             ,(227,'Tunisia','TN','TUN')
                             ,(228,'Turkey','TR','TUR')
                             ,(229,'Turkmenistan','TM','TKM')
                             ,(230,'Turks and Caicos Islands','TC','TCA')
                             ,(231,'Tuvalu','TV','TUV')
                             ,(232,'Uganda','UG','UGA')
                             ,(233,'Ukraine','UA','UKR')
                             ,(234,'United Arab Emirates','AE','ARE')
                             ,(235,'United Kingdom','GB','GBR')
                             ,(236,'United States','US','USA')
                             ,(237,'United States Minor Outlying Islands','UM','UMI')
                             ,(238,'Uruguay','UY','URY')
                             ,(239,'Uzbekistan','UZ','UZB')
                             ,(240,'Vanuatu','VU','VUT')
                             ,(241,'Vatican City','VA','VAT')
                             ,(242,'Venezuela','VE','VEN')
                             ,(243,'Vietnam','VN','VNM')
                             ,(244,'Virgin Islands, British','VG','VGB')
                             ,(245,'Virgin Islands, US','VI','VIR')
                             ,(246,'Wallis and Futuna','WF','WLF')
                             ,(247,'Western Sahara','EH','ESH')
                             ,(248,'Yemen','YE','YEM')
                             ,(249,'Zambia','ZM','ZMB')
                             ,(250,'Zimbabwe','ZW','ZWE');


INSERT INTO role VALUES (1, 'ADMIN'), (2, 'RESELLER'), (3, 'USER');

INSERT into gateway VALUES (1, 'STRIPE'), (2, 'PAYPAL'), (3, 'COINBASE'), (4, 'PARSPAL');

insert into reseller_level_coefficients(id, month_credit_percent, month_credit_max, current_credit_percent, current_credit_max,
                                        active_subscription_percent, active_subscription_max, membership_duration_percent, membership_duration_max,
                                        deposit_interval_manual_days, deposit_interval_max, total_sale_percent,total_sale_max, month_sale_percent, month_sale_max)
VALUES (1, '100', '30', '100', '20' , '100', '15', '100', '5', '30', '15', '100', '10', '100', '5');


insert into reseller_level(id, name, discount_percent, min_score) VALUES
(1, 'STARTER', '0','0'),
(2, 'BRONZE', '5','5'),
(3, 'SILVER','15', '15'),
(4, 'GOLD','30','30'),
(5,'DIAMOND','50','50'),
(6,'OWNER','100','100');



INSERT into user (id, username, email, password, rad_access, role_id, enabled) VALUES (1, 'nima@orb.group', 'nima@orb.group', '$2a$12$4.IiwafezzxBkzQ6ojigQufkMAVeSK588xrF0e.FD.Ol5EUnZzegi', 'rad',1, true);
INSERT into reseller(id, user_id, credit, level_id, enabled, created_at, updated_at) VALUES (1, 1, '100.00', 6, true, NOW(), NOW());
UPDATE user set reseller_id = 1 where id = 1;

INSERT into user (id, username, email, password, rad_access, role_id, enabled) VALUES (2, 'hosein@email.com', 'hosein@email.com', '$2a$12$4.IiwafezzxBkzQ6ojigQufkMAVeSK588xrF0e.FD.Ol5EUnZzegi', 'rad',2, true);
INSERT into reseller(id, user_id, credit, level_id, enabled, created_at, updated_at) VALUES (2, 2, '0', 1, true, NOW(), NOW());
UPDATE user set reseller_id = 2 where id = 2;

INSERT into user (id, username, email, password, rad_access, role_id, enabled) VALUES (3, 'alireza@email.com', 'alireza@email.com', '$2a$12$4.IiwafezzxBkzQ6ojigQufkMAVeSK588xrF0e.FD.Ol5EUnZzegi', 'rad',2, true);
INSERT into reseller(id, user_id, credit, level_id, enabled, created_at, updated_at) VALUES (3, 3, '0', 1, true, NOW(), NOW());
UPDATE user set reseller_id = 3 where id = 3;

INSERT into service_group(id, name, description, discount, created_at, updated_at) VALUES (1, 'Worldwide', 'Worldwide', '0', NOW(), NOW());
INSERT into service_group(id, name, description, discount, created_at, updated_at) VALUES (2, 'Iran', 'Iran', '70', NOW(), NOW());

insert into service_group_allowed_geolocations(service_group_id, allowed_geolocations_id) VALUES (2, 104);

insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (1, 1);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (1, 2);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (2, 1);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (2, 2);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (3, 1);
insert into reseller_service_groups(reseller_id, service_groups_id) VALUES (3, 2);

insert into group_app(id, service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, 1, 'None', 'Trial', 'tag-name', '0',0, 0);
insert into group_app(id, service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 2, 'Trial', 'Trial', 'tag-name', '0',7, 2);

insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '3 Month', '3 Month', 'tag-name', '16',90, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '6 Month', '6 Month', 'tag-name', '28',180, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '1 Year', '1 Year', 'tag-name', '49.08', 365, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '2 Year', '2 Year', 'tag-name', '84', 730, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, '3 Year', '3 Year', 'tag-name', '119', 1095, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (1, 'Lifetime', 'Lifetime', 'tag-name', '799', 7300, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group) VALUES (1, 'Basic Monthly', 'Basic Monthly', 'tag-name', '3.99', 30, 1, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Premium Monthly', 'Premium Monthly', 'tag-name', '6.99', 30, 5, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Premium Family Monthly', 'Premium Family Monthly', 'tag-name', '11.99', 30, 25, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Basic Yearly', 'Basic Yearly', 'tag-name', '21', 365, 1, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Premium Yearly', 'Premium Yearly', 'tag-name', '49.08', 365, 5, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count, registration_group ) VALUES (1, 'Premium Family Yearly', 'Premium Family Yearly', 'tag-name', '79.08', 365, 25, 1);

insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '3 Month', '3 Month', 'tag-name', '16',90, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '6 Month', '6 Month', 'tag-name', '28',180, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '1 Year', '1 Year', 'tag-name', '49.08', 365, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '2 Year', '2 Year', 'tag-name', '84', 730, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, '3 Year', '3 Year', 'tag-name', '119', 1095, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Lifetime', 'Lifetime', 'tag-name', '799', 7300, 2);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Basic Monthly', 'Basic Monthly', 'tag-name', '3.99', 30, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Premium Monthly', 'Premium Monthly', 'tag-name', '6.99', 30, 5);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Premium Family Monthly', 'Premium Family Monthly', 'tag-name', '11.99', 30, 25);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Basic Yearly', 'Basic Yearly', 'tag-name', '21', 365, 1);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Premium Yearly', 'Premium Yearly', 'tag-name', '49.08', 365, 5);
insert into group_app(service_group_id, name, description, tag_name, price, duration, multi_login_count ) VALUES (2, 'Premium Family Yearly', 'Premium Family Yearly', 'tag-name', '79.08', 365, 25);

INSERT INTO radius.server (id, city, country, created_at, description, host_name, kill_command, ports, private_ip, public_ip, root_command, secret, ssh_key, ssh_username, type, updated_at, ssh_private_key) VALUES

(1,'Sao Paulo','BR','2021-06-10 00:15:12.119251','Radius Client','br-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.12.149','18.230.148.147','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-10 00:15:12.119251',NULL),

(2,'Virginia','US','2021-06-13 22:08:32.635036','Radius Client','us-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.63.182','100.25.181.80','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:08:32.635036',NULL),

(3,'Ohio','US','2021-06-13 22:09:35.289589','Radius Client','usoh-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.17.55','3.131.119.158','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:09:35.289589',NULL),

(4,'Califronia','US','2021-06-13 22:19:30.642299','Radius Client','usca-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.9.102','18.144.81.121','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:19:30.642299',NULL),

(5,'Oregan','US','2021-06-13 22:20:38.133710','Radius Client','usor-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.2.174','34.209.25.245','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:20:38.133710',NULL),

(6,'Cape Town','ZA','2021-06-13 22:22:42.407702','Radius Client','za-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.22.229','13.245.16.178','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:22:42.407702',NULL),

(7,'Hong Kong','HK','2021-06-13 22:23:51.032822','Radius Client','hk-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.43.55','18.162.225.96','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:23:51.032822',NULL),

(8,'Mumbai','IN','2021-06-13 22:25:08.006835','Radius Client','in-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.6.3','13.233.79.2','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:25:08.006835',NULL),

(9,'Osaka','JP','2021-06-13 22:26:14.146151','Radius Client','jp-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.45.39','13.208.251.171','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:26:14.146151',NULL),

(10,'Seoul','SK','2021-06-13 22:27:26.745636','Radius Client','sk-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.44.152','3.35.209.228','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:27:26.745636',NULL),

(11,'Singapore','SG','2021-06-13 22:28:31.694074','Radius Client','sg-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.42.31','54.254.183.29','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:28:31.694074',NULL),

(12,'Sydney','AU','2021-06-13 22:37:18.177728','Radius Client','au-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.18.42','13.236.208.66','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:37:18.177728',NULL),

(13,'Tokyo','JP','2021-06-13 22:39:20.741595','Radius Client','jpto-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.43.176','52.194.225.49','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:39:20.741595',NULL),

(14,'Toronto','CA','2021-06-13 22:40:47.781043','Radius Client','ca-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.24.224','3.96.216.2','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:40:47.781043',NULL),

(15,'Frankfurt','DE','2021-06-13 22:41:54.070640','Radius Client','de-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.29.92','18.184.52.226','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:41:54.070640',NULL),

(16,'Dublin','IE','2021-06-13 22:42:58.167568','Radius Client','ie-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.12.96','34.244.240.144','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:42:58.167568',NULL),

(17,'London','UK','2021-06-13 22:47:22.512046','Radius Client','uk.iraip.com','sudo occtl disconnect user {username}',22,'172.31.14.150','18.133.184.96','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:47:22.512046',NULL),

(18,'Milan','IT','2021-06-13 22:49:24.028300','Radius Client','it-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.35.228','15.161.46.161','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:49:24.028300',NULL),

(19,'Paris','FR','2021-06-13 22:52:24.506459','Radius Client','fr-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.15.85','35.180.247.61','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:52:24.506459',NULL),

(20,'Stockholm','SE','2021-06-13 22:54:11.977614','Radius Client','se-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.3.103','13.53.45.189','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:54:11.977614',NULL),

(21,'Manama','BH','2021-06-13 22:55:27.384371','Radius Client','bh-cl.iraip.com','sudo occtl disconnect user {username}',22,'172.31.20.213','157.175.70.188','','Ay9W2QsUrhJ0bC63',NULL,'ec2-user','other','2021-06-13 22:55:27.384371',NULL),

(22,'Tehran','IR','2021-06-14 17:02:08.470614','Radius Client','ir-cl.iraip.com','sudo occtl disconnect user {username}',22,'213.232.125.35','213.232.125.35','','Ay9W2QsUrhJ0bC63','GbnratfTXp1gxVxm/JDoAA==cX9UbjLfbWXaAWyJYxTy0Q==','root','other','2021-06-14 17:02:08.470614',NULL),

(23,'London','UK','2021-06-14 17:03:47.299063','Radius Client','uk-cl.iraip.com','vpn-sessiondb logoff {username}',22,'178.239.168.62','178.239.168.62','enable','Ay9W2QsUrhJ0bC63','','OrbVPN','cisco','2021-06-14 17:03:47.299063',NULL);

insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (1, '18.230.148.147', 'br-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (2, '100.25.181.80', 'us-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (3, '3.131.119.158', 'usoh-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (4, '18.144.81.121', 'usca-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (5, '34.209.25.245', 'usor-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (6, '13.245.16.178', 'za-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (7, '18.162.225.96', 'hk-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (8, '13.233.79.2', 'in-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (9, '13.208.251.171', 'jp-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (10, '3.35.209.228', 'sk-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (11, '54.254.183.29', 'sg-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (12, '13.236.208.66', 'au-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (13, '52.194.225.49', 'jpto-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (14, '3.96.216.2', 'ca-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (15, '18.184.52.226', 'de-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (16, '34.244.240.144', 'ie-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (17, '18.133.184.96', 'uk.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (18, '15.161.46.161', 'it-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (19, '35.180.247.61', 'fr-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (20, '13.53.45.189', 'se-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (21, '157.175.70.188', 'bh-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (22, '213.232.125.35', 'ir-cl.iraip.com', 'other', null, 'Ay9W2QsUrhJ0bC63', null, null, null);
insert into radius.nas (id, nasname, shortname, type, ports, secret, server, community, description) values (23, '178.239.168.62', 'uk-cl.iraip.com', 'cisco', null, 'Ay9W2QsUrhJ0bC63', null, null, null);



#
# In your Quartz properties file, you'll need to set
# org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
CREATE TABLE QRTZ_JOB_DETAILS
(
    SCHED_NAME        VARCHAR(120) NOT NULL,
    JOB_NAME          VARCHAR(190) NOT NULL,
    JOB_GROUP         VARCHAR(190) NOT NULL,
    DESCRIPTION       VARCHAR(250) NULL,
    JOB_CLASS_NAME    VARCHAR(250) NOT NULL,
    IS_DURABLE        VARCHAR(1)   NOT NULL,
    IS_NONCONCURRENT  VARCHAR(1)   NOT NULL,
    IS_UPDATE_DATA    VARCHAR(1)   NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1)   NOT NULL,
    JOB_DATA          BLOB         NULL,
    PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_TRIGGERS
(
    SCHED_NAME     VARCHAR(120) NOT NULL,
    TRIGGER_NAME   VARCHAR(190) NOT NULL,
    TRIGGER_GROUP  VARCHAR(190) NOT NULL,
    JOB_NAME       VARCHAR(190) NOT NULL,
    JOB_GROUP      VARCHAR(190) NOT NULL,
    DESCRIPTION    VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13)   NULL,
    PREV_FIRE_TIME BIGINT(13)   NULL,
    PRIORITY       INTEGER      NULL,
    TRIGGER_STATE  VARCHAR(16)  NOT NULL,
    TRIGGER_TYPE   VARCHAR(8)   NOT NULL,
    START_TIME     BIGINT(13)   NOT NULL,
    END_TIME       BIGINT(13)   NULL,
    CALENDAR_NAME  VARCHAR(190) NULL,
    MISFIRE_INSTR  SMALLINT(2)  NULL,
    JOB_DATA       BLOB         NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
        REFERENCES QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
(
    SCHED_NAME      VARCHAR(120) NOT NULL,
    TRIGGER_NAME    VARCHAR(190) NOT NULL,
    TRIGGER_GROUP   VARCHAR(190) NOT NULL,
    REPEAT_COUNT    BIGINT(7)    NOT NULL,
    REPEAT_INTERVAL BIGINT(12)   NOT NULL,
    TIMES_TRIGGERED BIGINT(10)   NOT NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_CRON_TRIGGERS
(
    SCHED_NAME      VARCHAR(120) NOT NULL,
    TRIGGER_NAME    VARCHAR(190) NOT NULL,
    TRIGGER_GROUP   VARCHAR(190) NOT NULL,
    CRON_EXPRESSION VARCHAR(120) NOT NULL,
    TIME_ZONE_ID    VARCHAR(80),
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
    SCHED_NAME    VARCHAR(120)   NOT NULL,
    TRIGGER_NAME  VARCHAR(190)   NOT NULL,
    TRIGGER_GROUP VARCHAR(190)   NOT NULL,
    STR_PROP_1    VARCHAR(512)   NULL,
    STR_PROP_2    VARCHAR(512)   NULL,
    STR_PROP_3    VARCHAR(512)   NULL,
    INT_PROP_1    INT            NULL,
    INT_PROP_2    INT            NULL,
    LONG_PROP_1   BIGINT         NULL,
    LONG_PROP_2   BIGINT         NULL,
    DEC_PROP_1    NUMERIC(13, 4) NULL,
    DEC_PROP_2    NUMERIC(13, 4) NULL,
    BOOL_PROP_1   VARCHAR(1)     NULL,
    BOOL_PROP_2   VARCHAR(1)     NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_BLOB_TRIGGERS
(
    SCHED_NAME    VARCHAR(120) NOT NULL,
    TRIGGER_NAME  VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    BLOB_DATA     BLOB         NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    INDEX (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
        REFERENCES QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_CALENDARS
(
    SCHED_NAME    VARCHAR(120) NOT NULL,
    CALENDAR_NAME VARCHAR(190) NOT NULL,
    CALENDAR      BLOB         NOT NULL,
    PRIMARY KEY (SCHED_NAME, CALENDAR_NAME)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
(
    SCHED_NAME    VARCHAR(120) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    PRIMARY KEY (SCHED_NAME, TRIGGER_GROUP)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_FIRED_TRIGGERS
(
    SCHED_NAME        VARCHAR(120) NOT NULL,
    ENTRY_ID          VARCHAR(95)  NOT NULL,
    TRIGGER_NAME      VARCHAR(190) NOT NULL,
    TRIGGER_GROUP     VARCHAR(190) NOT NULL,
    INSTANCE_NAME     VARCHAR(190) NOT NULL,
    FIRED_TIME        BIGINT(13)   NOT NULL,
    SCHED_TIME        BIGINT(13)   NOT NULL,
    PRIORITY          INTEGER      NOT NULL,
    STATE             VARCHAR(16)  NOT NULL,
    JOB_NAME          VARCHAR(190) NULL,
    JOB_GROUP         VARCHAR(190) NULL,
    IS_NONCONCURRENT  VARCHAR(1)   NULL,
    REQUESTS_RECOVERY VARCHAR(1)   NULL,
    PRIMARY KEY (SCHED_NAME, ENTRY_ID)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_SCHEDULER_STATE
(
    SCHED_NAME        VARCHAR(120) NOT NULL,
    INSTANCE_NAME     VARCHAR(190) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13)   NOT NULL,
    CHECKIN_INTERVAL  BIGINT(13)   NOT NULL,
    PRIMARY KEY (SCHED_NAME, INSTANCE_NAME)
)
    ENGINE = InnoDB;

CREATE TABLE QRTZ_LOCKS
(
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40)  NOT NULL,
    PRIMARY KEY (SCHED_NAME, LOCK_NAME)
)
    ENGINE = InnoDB;

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS (SCHED_NAME, REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS (SCHED_NAME, JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS (SCHED_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS (SCHED_NAME, CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_GROUP, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS (SCHED_NAME, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_STATE, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS (SCHED_NAME, MISFIRE_INSTR, NEXT_FIRE_TIME, TRIGGER_GROUP,
                                                             TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, INSTANCE_NAME, REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS (SCHED_NAME, TRIGGER_GROUP);

commit;
