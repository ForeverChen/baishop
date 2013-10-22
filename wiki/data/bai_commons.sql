/*
SQLyog Ultimate v9.62 
MySQL - 5.5.17 : Database - bai_commons
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`bai_commons` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `bai_commons`;

/*Table structure for table `bai_enum` */

DROP TABLE IF EXISTS `bai_enum`;

CREATE TABLE `bai_enum` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `pid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '父id',
  `path` varchar(255) NOT NULL DEFAULT '' COMMENT '树路径',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:业务群 1:参数 2:枚举 3:枚举元素',
  `key` varchar(64) NOT NULL DEFAULT '' COMMENT '键,英文,一般用于检索的键',
  `text` varchar(64) NOT NULL DEFAULT '' COMMENT '名称,中文,一般用于显示，也可以用于检索',
  `value` varchar(4000) NOT NULL DEFAULT '' COMMENT '数据值,一般存储比较大的数据',
  `sort` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_modules_sort` (`sort`) USING BTREE,
  KEY `idx_modules_text` (`text`),
  KEY `idx_modules_code` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='后台管理系统功能模块表';

/*Data for the table `bai_enum` */

insert  into `bai_enum`(`id`,`pid`,`path`,`type`,`key`,`text`,`value`,`sort`) values (1,0,'1/',0,'sys_common','通用业务','',1),(3,1,'1/3/',1,'param_keywords','网站搜索关键字','BAI 百鞋诚，互联网时尚品牌。根植互联网，全球时尚潮流，国际一线品质，平民价位。在线销售男装、女装、童装、鞋、家居、配饰等。送货上门、货到付款，无条件退换货。',13),(4,1,'1/3/',1,'param_description','网站搜索描述','BAI,百鞋诚, 快时尚,男装,女装,童装,鞋,家居,配饰,衬衫,牛津纺,牛津纺衬衫,衬衣,长袖衬衫,短袖衬衫,全棉,纯棉,百分百棉,100%棉,全棉衬衫,纯棉衬衫,全棉衬衣,纯棉衬衣,免烫,免熨,免烫衬衫,免熨衬衫,免烫衬衣,免熨衬衣,牛津纺衬衣,领尖扣,直领,小方领,POLO,短袖POLO,长袖POLO,条纹POLO,素色POLO,T恤,圆领T恤,VT,圆领T,印花T,文化衫,卫衣,打底衫,高领衫,低领,鞋,凉鞋,皮鞋,板鞋,商务皮鞋,正装皮鞋,滑板鞋,潮鞋,休闲皮鞋,帆布鞋,运动鞋,运动休闲鞋,家居鞋,雪地靴,靴子,平底鞋,沙滩鞋,夹脚鞋,圆头,尖头,女鞋,休闲鞋,男鞋,童鞋,花园鞋,丝袜,长筒袜,连裤袜,网袜,天鹅绒,瘦腿袜,中筒袜,筒袜,棉袜,靴袜,打底裤,羽绒服,项链,手镯,围巾,棉线衫,开衫,针织衫,外套,西服,休闲西服,夹克,毛衣,背心,毛背心,裤子,长裤,休闲裤,牛仔裤,牛仔,卡其裤,直筒休闲裤,直筒卡其裤,免烫休闲裤,免烫卡其裤,斜纹休闲裤,斜纹卡其裤,短裤,沙滩裤,内衣,内裤,秋衣,秋裤,三角裤,平角裤,领带,袜子,家居,浴巾,面巾,毛巾,收纳,户外,床品,伞,餐垫,拖鞋,盖毯,断码,打折',14),(5,1,'1/5/',2,'enum_yesno','是和否','',15),(6,5,'1/5/6/',3,'no','否','0',56),(7,5,'1/5/7/',3,'yes','是','1',57),(8,1,'1/8/',2,'enum_yesnocancel','是、否、取消','',18),(9,8,'1/8/9/',3,'no','否','0',89),(10,8,'1/8/10/',3,'yes','是','1',810),(11,8,'1/8/11/',0,'cancel','取消','2',811);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
