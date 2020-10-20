/**
 * @author
 * @version
 * 2018年7月24日 下午4:45:16
 */
package com.icloud.config.quartz.job;

import com.icloud.modules.job.task.ITask;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

/**
 * 类名称: DealSendPrizeFairJob
 * 类描述: 处理用户过期数据
 * 创建人: zhangdehai
 * 创建时间:2018年7月24日 下午4:45:16
 */
@Component
@EnableScheduling
public class DealSendPrizeFairJob implements ITask {

	public final static Logger log = LoggerFactory.getLogger(DealSendPrizeFairJob.class);
	
//	@Autowired
//
//	private DealUserValidMoneyService dealUserValidMoneyService;
//CRON表达式 含义
//“0 0 12 * * ?” 每天中午十二点触发
//“0 15 10 ? * *” 每天早上10：15触发
//“0 15 10 * * ?” 每天早上10：15触发
//“0 15 10 * * ? *” 每天早上10：15触发
//“0 15 10 * * ? 2005” 2005年的每天早上10：15触发
//“0 * 14 * * ?” 每天从下午2点开始到2点59分每分钟一次触发
//“0 0/5 14 * * ?” 每天从下午2点开始到2：55分结束每5分钟一次触发
//“0 0/5 14,18 * * ?” 每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发
//“0 0-5 14 * * ?” 每天14:00至14:05每分钟一次触发
//“0 10,44 14 ? 3 WED” 三月的每周三的14：10和14：44触发
//“0 15 10 ? * MON-FRI” 每个周一、周二、周三、周四、周五的10：15触发

//	秒（0~59）
//	分钟（0~59）
//	小时（0~23）
//	天（月）（0~31，但是你需要考虑你月的天数）
//	月（0~11）
//	天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
//	年份（1970－2099）


    //每1分钟运行一次（秒 分 时 ）
//    @Scheduled(cron = "0 0/1 * * * ?")
	//每天9点运行
//	@Scheduled(cron = "0 0 9  * * ? ")
	//每20分钟运行一次（秒 分 时 ）
//	@Scheduled(cron = "0 0/10 * * * ?")
	//每5妙运行一次
//	@Scheduled(cron = "0/5 * * * * ?")
	public void todDalSendPrizeFairJob() throws Exception{
		log.info("===============todDalSendPrizeFairJob running===============");
//        dealUserValidMoneyService.dealWithUserValidMoney(false);
		log.info("===============todDalSendPrizeFairJob end ===============");
	}



	@Override
	public void run(String params) {
		try {
			todDalSendPrizeFairJob();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
