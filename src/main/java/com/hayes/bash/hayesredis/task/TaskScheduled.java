package com.hayes.bash.hayesredis.task;

import com.hayes.bash.hayesredis.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskScheduled {

    @Autowired
    private BusinessService businessService ;


    @Scheduled(cron = "0 0 3 * * ?")
//    @Scheduled(fixedRate = 1000*3600*24)
    public void refreshAllData(){
        try {

            businessService.refreshAllData();

        }catch (Exception e ){

            e.printStackTrace();

        }
    }


}
