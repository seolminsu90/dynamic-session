# dynamic-session
sqlsessiontemplate을 설정파일을 다양하게 조절해서 쓸 수 있도록 할 필요가 생겨서 만들어봄

- yml 파일에 datasource를 여러개 규격에 맞게 등록하면 알아서 찾아 쓸 수 있도록 설정함
- 내 업무환경에선 datasource를 넣었다 뺐다 매주 반복되는 일이 있어서.. 사용
- db리스트 선제적 찾기 필요시엔 @DependsOn or @Order 로 생성순서 조절

## 더 세련된 방법

- AbstractRoutingDataSource 구현

```bash
public class MyRoutingDataSource extends AbstractRoutingDataSource {
  // 이 메서드를 통해 데이터 소스를 선택하는 키(objcet)를 선택하는 방법을 정한다.
	@Override
	protected Object determineCurrentLookupKey() {
        // master/slave cluster 설정 시 
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (isReadOnly) {
            return "slave";
        } else {
            return "master";
        }	
        
        /* key (ex. game world). 
        
          ThreadLocal을 필요한 키를 가진 static class로 구현해서 사용 ( private static ThreadLocal<String> world = new ThreadLocal<>(); )
        
          다음과 같이 사용
          StaticThreadLocal.set("world1"); - ThreadLocal 월드 값 설정
          routingMapper.crudQuery(); - 매퍼 쿼리 실행
          - 필요에 따라 ThreadLocal remove() 처리
        */
        return StaticThreadLocal.get();
	}
}
```
```bash
public class StaticThreadLocal {
	private static ThreadLocal<String> world = new ThreadLocal<>();
	public String get(){
		return world.get();
	}
	public void set(String worldId){
		world.set(worldId);
	}
	public void remove(){
		world.remove();
	}
}
```

- 데이터 소스 구현
```bash
 @Bean("RoutingDataSource")
    public DataSource routingDataSource() {
        //아래 datasource 들은 타 단일 데이터소스(ex. commondb)에 연결된 DB에서 목록을 읽어오게 하면 DB 상 데이터로 동적으로 처리가 된다. (물론 Ordering필요)
        HikariDataSource world1Datasource = createDatasource(~~);
        HikariDataSource world2Datasource = createDatasource(~~);

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("world1", world1Datasource);
        dataSourceMap.put("world2", world2Datasource);

        DynamicRoutingDataSource routingDataSource = new MyRoutingDataSource();
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(world1Datasource); // 없어도되나? 안해봄
        return routingDataSource;
    }
    
    // Master/Slave 방식 때 Lazy가 필요하다는데 트랜젝션을 위해. TransactionSynchronizationManager.isCurrentTransactionReadOnly 필요없으면 굳이일듯?
    @Bean("routingLazyDataSource")
    public DataSource routingLazyDataSource(@Qualifier("RoutingDataSource") DataSource dataSource) {
        return new LazyConnectionDataSourceProxy(dataSource);
    }
    
    public HikariDataSource createDatasource() {
    	
        // 데이터 connection 정보
        ~~ Builder 있나
        return new HikariDataSource(config);
    }
    
    ~~ SqlSessionFactory... SqlSessionTemplate(mybatis)... TransactionManager... 는 위의 RoutingDataSource를 Qualifier 하여 이어주면 된다.
    ~~ Mapper 등도 여기에서 경로 설정 해주거나.. @MapperScan @Mapper 사용
```
