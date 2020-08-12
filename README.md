# redis设计

## 1. 将phone_number_info表数据放进redis 。 
**存储类型**： Hash
**key：**phone_number 
**value:** phone_number_info record 

-- **该号码符合的规则** 例如： rule_sets：AAAA，8888，811000,1700881,    

## 2. 建索引。 
**索引存储类型：** Set
**key：** 索引类型英文缩写： AAAA 
**value:** phone_number数组

====

索引类型包含：
1. 规则 

```xml
<if test='ruleType == "AAA" 	or  ruleType == "AAAA" 	or  ruleType == "ABAB" 	or
		  ruleType == "ABCD"  	or  ruleType == "520" 	or
		  ruleType == "1314" 	or  ruleType == "4" 	or  ruleType == "AAA6" 	or
		  ruleType == "AAAB" 	or  ruleType == "ABBA" 	or  ruleType == "no4"  '>
	and t.rule = #{ruleType,jdbcType=VARCHAR}
</if>
<if test="ruleGetType == 'LIKE'">
	and t.phone_number like concat('%',#{ruleType,jdbcType=VARCHAR} )
	-- %521,%920,%921,%1212,%9421,%0006,%1023,%1117,%7337,%8128
</if>
<if test="ruleGetType == 'YEAR'">
	and t.phone_number like concat(concat('%',#{ruleType,jdbcType=VARCHAR}),'_')
	-- %195_ ,%196_ ,%197_,%198_,%199_,%200_,%201_
</if>

```
1. 号段

```sql
SELECT SEGMENT, COUNT(1) FROM phone_number_info GROUP BY SEGMENT 
```
1. 城市列表

```sql
SELECT PROVINCE_NUMBER , CITY_NUMBER  , COUNT(1) FROM phone_number_info GROUP BY PROVINCE_NUMBER , CITY_NUMBER  ORDER BY PROVINCE_NUMBER  , CITY_NUMBER
```
1. 搜索（尾号4位）  

```sql
SELECT SUBSTR(PHONE_NUMBER,8,4 ) , count(1) FROM PHONE_NUMBER_INFO pni GROUP BY SUBSTR(PHONE_NUMBER,8,4 ) 
```
1. 价格

```sql
/** 100以下，100-500 ， 500-1000， 1000-5000，5000-10000，1000以上 **/
/** price_A，price_B ， price_C， price_D，price_E，price_F **/

```


## 查询： 
1. 单条件查询
> 通过规则key查询set， 用set包含的号码查询record

1. 复合条件查询
> 通过规则key查询set，取交集， 用交集包含的号码查询record


## 更改（开户回调）
1. 移除 phoneNumber.record 记录的某个值 
2. 更改索引 -（该号码被开户）
    通过上面的rule_sets， 查询到对应的key，从数组中移除该号码
