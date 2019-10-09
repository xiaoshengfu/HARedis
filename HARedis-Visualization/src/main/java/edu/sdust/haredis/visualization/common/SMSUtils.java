package edu.sdust.haredis.visualization.common;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * ClassName: SMSUtils
 * @Description: 阿里云短信工具类
 * @author xiaoshengfu(2439323118@qq.com)
 * @date 2019年3月8日 下午12:57:40
 */
public class SMSUtils {

	// 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
	static final String accessKeyId = "";
	static final String accessKeySecret = "";

	/**
	 * @Description: 发送短信验证码
	 * @param telephone
	 * @param signName
	 * @param templateCode
	 * @param code
	 * @return
	 * @throws ClientException SendSmsResponse 
	 * @throws
	 * @author xiaoshengfu(2439323118@qq.com)
	 * @date 2019年3月8日 下午12:57:25
	 */
	public static SendSmsResponse sendSMSVerificationCode(String telephone, String signName, String templateCode,
			String code) throws ClientException {
		SendSmsResponse sendSmsResponse = null;
		// 初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
		IAcsClient acsClient = new DefaultAcsClient(profile);

		// 组装请求对象-具体描述见控制台-文档部分内容
		SendSmsRequest request = new SendSmsRequest();
		// 必填:待发送手机号
		request.setPhoneNumbers(telephone);
		// 必填:短信签名-可在短信控制台中找到"回收猫"
		request.setSignName(signName);
		// 必填:短信模板-可在短信控制台中找到"SMS_126150015"
		request.setTemplateCode(templateCode);
		// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		request.setTemplateParam("{'code':'" + code + "'}");
		// hint 此处可能会抛出异常，注意catch
		try {
			sendSmsResponse = acsClient.getAcsResponse(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendSmsResponse;
	}
}
