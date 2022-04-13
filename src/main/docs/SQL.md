```postgres-sql
WITH 
t as (select dayvalues.symbol as sym, *, row_number() over (partition by dayvalues.symbol order by stime desc) as rn 
    from dayvalues
    join fundamentals on dayvalues.symbol = fundamentals.symbol 
    ),
tt as (select *  from t 
    where t.rn <= 7
	order by sym asc
	),
avg_gain_with_industry as (
	select avg((tt.sclose-tt.sopen)/tt.sopen) as avg_gain, tt.sym, jsonb_path_query(tt.data, '$.sector') as ind from tt group by tt.sym, ind order by avg_gain desc
	),
fastest_industry as (
	select avg(avg_gain) as g, ind from avg_gain_with_industry group by ind
)
	select * from fastest_industry order by g desc ;
```


```postgres-sql
WITH 
t as (select dayvalues.symbol as sym, *, row_number() over (partition by dayvalues.symbol order by stime desc) as rn 
    from dayvalues
    join fundamentals on dayvalues.symbol = fundamentals.symbol 
    ),
tc as (select *  from t 
    where t.rn = 1
	order by sym asc
	),
tc62 as (select avg(t.sclose) as sclose, sym  from t 
    where t.rn <= 63
	group by t.sym
	order by sym asc
	),
tc126 as (select avg(t.sclose) as sclose, sym  from t 
    where t.rn <= 126
	group by t.sym
	order by sym asc
	),
rs as (
	select (2*tc.sclose/tc62.sclose) + (tc.sclose/tc126.sclose) as strength, tc.sym, tc.sclose as sclose_s, tc62.sclose as tc62_s, tc126.sclose as tc126_s from tc 
		join tc62 on tc.sym = tc62.sym 
		join tc126 on tc.sym = tc126.sym
	)

	select * from rs order by strength desc ;
```

# Delete all stocks with NaN values
```postgres-sql
delete from dayvalues where symbol IN (select distinct symbol from dayvalues where sopen = 'NaN');
```