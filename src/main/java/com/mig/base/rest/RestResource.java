package com.mig.base.rest;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mig.base.db1.pojo.AttrsJpo;
import com.mig.base.db1.pojo.TestJpo;
import com.mig.base.db1.repo.AttrsRepository;
import com.mig.base.db1.repo.TestRepository;
import com.mig.base.wony.repo.WonyAttrsRepository;
import com.mig.base.wony.repo.WonyTestRepository;

@RestController
@RequestMapping(value="/")
@Transactional(propagation=Propagation.REQUIRED)
public class RestResource {
	private int groupCnt = 10;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TestRepository testRepository;
	@Autowired
	private AttrsRepository attrsRepository;
	
	@Autowired
	private WonyTestRepository wonyTestRepository;
	@Autowired
	private WonyAttrsRepository wonyAttrsRepository;

	@GetMapping(value="/home")
	public String home() {
		System.out.println("HIBERNATE VERSION = "+org.hibernate.Version.getVersionString());
		return "MIG HOME";
	}
	
	@GetMapping(value="/dpbas/{page}")
	public String getInterfaceInfo(@PathVariable("page") Integer page) {
		int index = 1;
		StopWatch watch = new StopWatch("Threads");
//		Gson gson = new GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create();
//		Type wonyInterfaceType = new TypeToken<WonyTestJpo>(){}.getType();
		
		LOGGER.debug("========== MIG START ========");
		watch.start("TEST Select All TASK");
		Page<TestJpo> data = this.testRepository.findAll(PageRequest.of(page, 10000));
		List<TestJpo> src = null;
		watch.stop();
		
		LOGGER.debug("\n1. TEST Select All RESULT : " + watch.prettyPrint());
		src = data.getContent();		
		
//		if(data.getTotalPages() > 0) {
//		while(data.hasContent()) {
		if(data.hasContent()) {
			src = data.getContent();
			LOGGER.debug("=========== Slice Size = "+data.getSize()+" ============");
			LOGGER.debug("=========== Slice Number = "+data.getNumber()+" ============");
			LOGGER.debug("=========== Element Number = "+data.getTotalElements()+" ============");
			LOGGER.debug("=========== Total Pages = "+data.getTotalPages()+" ============");
			
//			watch.start("TEST MIGRATION TASK "+page);			
//			wonyList = src.stream().parallel().map(e -> (WonyTestJpo) gson.fromJson(gson.toJson(e),wonyInterfaceType))
//			.collect(Collectors.toList());
//			LOGGER.debug("=========== wonyList = "+wonyList.size()+" ============");
//			WonyTestJpo wony =  gson.fromJson(gson.toJson(data.get()),wonyInterfaceType);
//			wonyTestRepository.save(wony);		
//			watch.stop();
//			LOGGER.debug("\n1. TEST MIGRATION RESULT : " + watch.prettyPrint());
			
			watch.start(" TEST MERGE TASK "+page);
			wonyTestRepository.saveAll(src);
			watch.stop();
			LOGGER.debug("\n1. TEST MERGE RESULT : " + watch.prettyPrint());
			
//			if(data.hasNext()) {
//				data = this.testRepository.findAll(data.nextPageable());
//			}
		}
		LOGGER.debug("========== MIG END ========");
		return watch.prettyPrint();
	}
	
	@GetMapping(value="/attr/{page}")
	public String getAttrInfo(@PathVariable("page") Integer page) {
		int index = 1;
		StopWatch watch = new StopWatch("Threads");
		
		LOGGER.debug("========== MIG START ========");
		watch.start("ATTRS Select All TASK");
		Page<AttrsJpo> data = this.attrsRepository.findAll(PageRequest.of(page,1000));
		List<AttrsJpo> src = null;
		watch.stop();
		
		LOGGER.debug("\n1. ATTRS Select All RESULT : " + watch.prettyPrint());
		src = data.getContent();		
		
//		if(data.getTotalPages() > 0) {
//		while(data.hasContent()) {
		if(data.hasContent()) {
			src = data.getContent();
			LOGGER.debug("=========== Slice Size = "+data.getSize()+" ============");
			LOGGER.debug("=========== Slice Number = "+data.getNumber()+" ============");
			LOGGER.debug("=========== Element Number = "+data.getTotalElements()+" ============");
			LOGGER.debug("=========== Total Pages = "+data.getTotalPages()+" ============");
			
//			watch.start("ATTRS MIGRATION TASK "+page);			
//			wonyList = src.stream().parallel().map(e -> (WonyAttrsJpo) gson.fromJson(gson.toJson(e),wonyInterfaceType))
//			.collect(Collectors.toList());
//			LOGGER.debug("=========== wonyList = "+wonyList.size()+" ============");
//			WonyTestJpo wony =  gson.fromJson(gson.toJson(data.get()),wonyInterfaceType);
//			wonyTestRepository.save(wony);		
//			watch.stop();
//			LOGGER.debug("\n1. ATTRS MIGRATION RESULT : " + watch.prettyPrint());
			
			watch.start(" ATTRS MERGE TASK "+page);
			wonyAttrsRepository.saveAll(src);
			watch.stop();
			
			LOGGER.debug("\n1. ATTRS MERGE RESULT : " + watch.prettyPrint());
//			page = page + 4;
//			if(page <= data.getTotalPages()) {
//				LOGGER.debug(" GO TO NEXT LOOP ");
//				data = this.attrsRepository.findAll(PageRequest.of(page, 100));
//			}else {
//				LOGGER.debug("========== END LOOP ========");
//				return watch.prettyPrint();
//			}
		}
		LOGGER.debug("========== MIG END ========");
		return watch.prettyPrint();
	}	
	
	@GetMapping(value="/interfaces/{page}")
	public String interfaces(@PathVariable("page") Integer page) {
		String tableName = "TEST";
		int index = 0;
		StopWatch watch = new StopWatch("Threads");		
		ExecutorService executor = Executors.newFixedThreadPool(groupCnt);
		List<CompletableFuture<Iterable<TestJpo>>> threadGroup = new ArrayList<>();
		  	
		LOGGER.debug("========== MIG START ========");
		watch.start(tableName+" Select All TASK");
		Page<TestJpo> data = this.testRepository.findAll(PageRequest.of(page, 1000));
		watch.stop();
		LOGGER.debug("\n1. TEST Select All RESULT : " + watch.prettyPrint());
		
		LOGGER.debug("=========== Total Element Number = "+data.getTotalElements()+" ============");
		LOGGER.debug("=========== Total Pages = "+data.getTotalPages()+" ============");
		
		watch.start(tableName+" INSERT TASK");
		while(data.hasContent()) {
			List<TestJpo> src = data.getContent();
			LOGGER.debug("=========== Thread Number = Thread_"+index+" ============");
			LOGGER.debug("=========== Slice Size = "+data.getSize()+" ============");
			LOGGER.debug("=========== Slice Number = "+data.getNumber()+" ============");
			
			threadGroup.add(CompletableFuture.supplyAsync(() -> wonyTestRepository.saveAll(src), executor));
						
			if(data.hasNext()) {
				index++;
				data = this.testRepository.findAll(data.nextPageable());
			}else {
				LOGGER.debug("==================== Time to Wait ===========================");
				CompletableFuture<Void> allFutures = CompletableFuture.allOf(threadGroup.toArray(new CompletableFuture[threadGroup.size()]));
				try {
					allFutures.get();
					threadGroup.clear();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				watch.stop();
				LOGGER.debug("\n1. "+tableName+" INSERT RESULT : " + watch.prettyPrint());				
				LOGGER.debug("========== MIG END ========");
				
				return tableName+" MIG DONE";					
			}			
		}
		LOGGER.debug("========== MIG END ========");
		return tableName+" MIG DONE";	
	}	
	
	@GetMapping(value="/attrs/{page}")
	public String attrs(@PathVariable("page") Integer page) {
		String tableName = "ATTRS";
		int index = 0;
		StopWatch watch = new StopWatch("Threads");
		ExecutorService executor = Executors.newFixedThreadPool(groupCnt);
		List<CompletableFuture<Iterable<AttrsJpo>>> threadGroup = new ArrayList<>();
		  	
		LOGGER.debug("========== MIG START ========");
		watch.start(tableName+" Select All TASK");
		Page<AttrsJpo> data = this.attrsRepository.findAll(PageRequest.of(page, 1000));
		watch.stop();		
		LOGGER.debug("\n1. "+tableName+" Select All RESULT : " + watch.prettyPrint());
		
		LOGGER.debug("=========== Total Element Number = "+data.getTotalElements()+" ============");
		LOGGER.debug("=========== Total Pages = "+data.getTotalPages()+" ============");
		
//		data = this.attrsRepository.findAll(PageRequest.of(250, 100));
//		while(data.hasContent()) {
		if(data.hasContent()) {
			List<AttrsJpo> src = data.getContent();
			LOGGER.debug("=========== Thread Number = Thread_"+index+" ============");
			LOGGER.debug("=========== Slice Size = "+data.getSize()+" ============");
			LOGGER.debug("=========== Slice Number = "+data.getNumber()+" ============");
			LOGGER.debug("=========================================================");
			
			threadGroup.add(CompletableFuture.supplyAsync(() -> wonyAttrsRepository.saveAll(src), executor)
								.exceptionally(exception -> {
									src.stream().forEach(a -> LOGGER.debug("TEST_ID = "+a.getDtNmId()));
									return null;
								})
							);
			
			if(data.hasNext()) {
				index++;
				data = this.attrsRepository.findAll(data.nextPageable());
			}else {
				CompletableFuture<Void> allFutures = CompletableFuture.allOf(threadGroup.toArray(new CompletableFuture[threadGroup.size()]));
				try {
					allFutures.get();
					threadGroup.clear();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				LOGGER.debug("========== MIG END ========");
				return tableName+" MIG DONE";				
			}			
			
			//20 개가 돌면 완료 될때 까지 대기
			if( index % 20 == 0) {
				LOGGER.debug("============ "+index % 20+ " ===============");
				LOGGER.debug("==================== Time to Wait ===========================");
				CompletableFuture<Void> allFutures = CompletableFuture.allOf(threadGroup.toArray(new CompletableFuture[threadGroup.size()]));
				try {
					allFutures.get();
					threadGroup.clear();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
				
		LOGGER.debug("========== MIG END ========");
		return tableName+" MIG DONE";
	}	
	
	@GetMapping(value="/sample")
	public String test() {
		LOGGER.debug("========== TEST START ========");
		String TableName = "ATTRS";

		List<Long> arg = new ArrayList<>();
		arg.add(new Long("516264"));
		Optional<List<AttrsJpo>> data = this.attrsRepository.findByDtNmIdIn(arg);
		if(data.isPresent()) {
			if(data.get().size() == 1) {
				LOGGER.debug("========== Let's try.... ========");
				AttrsJpo jpo = data.get().get(0);
				try {
					jpo.setStandardKoreanName(new String(convertToBinary(data.get().get(0).getStandardKoreanName(),"UTF-8"),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				wonyAttrsRepository.save(jpo);				
			}
		}
		LOGGER.debug("========== TEST END ========");
		return "TEST DONE";
	}
	
	private byte[] convertToBinary(String input, String encoding) throws UnsupportedEncodingException {
	    byte[] encoded_input = Charset.forName(encoding)
	      .encode(input)
	      .array();
	    return encoded_input;
	}	

}

