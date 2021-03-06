package com.github.coolcool.sloth.lianjiadb.timetask;

import com.github.coolcool.sloth.lianjiadb.common.MyHttpClient;
import com.github.coolcool.sloth.lianjiadb.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.TimerTask;

/**
 * Created by dee on 2016/11/19.
 */
@EnableScheduling
@Service
public class GenAndExeDailyProcessTimeTask extends TimerTask {


    private static final Logger log = LoggerFactory.getLogger(FetchHouseIndexTimeTask.class);

    @Autowired
    private ProcessService processService;

    @Value("${com.github.coolcool.sloth.lianjiadb.timetask.genprocess.hour:8}")
    int genprocessHour;

    static boolean genProcessing = false;
    static boolean houseUrlsFetching = false;

    @Override
    public void run() {

    }

    /**
     * 生成当天任务
     */
    @Scheduled(cron="0 0/5 * * * ?")
    public void gen() {
        //每天8点执行一次
        if(LocalTime.now().getHour() != genprocessHour)
            return;

        if(MyHttpClient.available && !genProcessing){
            genProcessing = true;
            log.info("开始执行genProcessing...");
            try {
                processService.genProcesses();
            }catch (Throwable t){
                t.printStackTrace();
            }
            genProcessing = false;
        }
    }



    /**
     * 根据当天的执行任务，按最小区域（车陂、华景）分页获取房屋链接地址，入库 houseindex
     */
    @Scheduled(cron="0 0/5 * * * ?")   //每5分钟执行一次
    public void exe() {
        if(MyHttpClient.available && !houseUrlsFetching){
            houseUrlsFetching = true;
            log.info("开始执行houseUrlsFetching...");
            try {
                processService.fetchHouseUrls();
            }catch (Throwable t){
                t.printStackTrace();
            }
            houseUrlsFetching = false;
        }
    }





}
