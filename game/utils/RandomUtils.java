package com.lbwan.game.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lbwan.game.exception.BusinessException;


public class RandomUtils {
	
	private static final Log logger = LogFactory.getLog(RandomUtils.class);
	
	public static final int DEFAULT_RANGE = 1000000;
	
	/**
	 * 是否会发生
	 * @param probability
	 * @param range
	 * @return
	 */
	public static boolean isHappened(int probability, int range){
		if(probability <= 0){
			return false;
		}
		if(probability >= range){
			return true;
		}
		int randomValue = getRandomValue(range);
		return randomValue < probability;
	}
	
	/**
	 * 获取随机值（指定区间）
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandomValue(int min, int max){
		int randomValue = getRandomValue(max - min + 1);
		return randomValue + min;
	}
	
	/**
	 * 获取多个随机值(从指定数据中）
	 * @param sourceValues 指定数据
	 * @param num 获取随机数的数量
	 * @return
	 */
	public static <T> List<T> getNotRepeatRandomValues(List<T> sourceValues, int num){
		if(sourceValues == null || sourceValues.size() == 0){
			return null;
		}
		
		List<T> cloneSourceValue = new ArrayList<T>(sourceValues);
		List<T> randomValues = new ArrayList<T>(num);
		for(int i=0; i<num; i++){
			T randomValue = getRandomValue(cloneSourceValue);
			randomValues.add(randomValue);
			
			cloneSourceValue.remove(randomValue);
		}
		
		return randomValues;
	}
	
	/**
	 * 获取一个随机值(从指定数据中）
	 * @param sourceValues 指定数据
	 * @return
	 */
	public static <T> T getRandomValue(List<T> sourceValues){
		if(sourceValues == null || sourceValues.size() == 0){
			return null;
		}
		
		int size = sourceValues.size();
		if(size == 1){
			return sourceValues.get(0);
		}
		int randomValue = getRandomValue(DEFAULT_RANGE);
		
		return sourceValues.get(randomValue % size);
	}
	
	/**
	 * 根据各概率，计算出
	 * @param values
	 * @param probabilitys
	 * @param range
	 * @return
	 */
	public static <T> T getRandomValue(List<T> sourceValues, List<Integer> probabilitys, int range){
		if(sourceValues == null || probabilitys == null){
			return null;
		}
		if(sourceValues.size() != probabilitys.size() ){
			throw new BusinessException("values 与  probabilitys 的大小不一致 ");
		}
		
		List<Integer> ranges = new ArrayList<Integer>();
		
		int start = 0;
		for (Integer probability : probabilitys) {
			start += probability;
			ranges.add(start);
		}
		
		if(start > range){
			logger.error("概率设置有错，各小概率的和大于总范围：" + start +"/" + range);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("ranges:" + ranges);
		}
		
		int randomValue = getRandomValue(range);
		
		for (int i=0; i<ranges.size(); i++){
			if(randomValue < ranges.get(i)){
				return sourceValues.get(i);
			}
		}
		
		return null;
	}
	
	public static int getRandomValue(int range){
		int randomValue = new Random().nextInt(range);
		return randomValue;
	}
}
