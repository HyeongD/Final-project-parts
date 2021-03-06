package com.BridginSystem.bridge.main.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kms.aliyun.credentials.*;



@Component
public class AlipayConfig implements ApplicationRunner {
	
	private static String app_id = "SANDBOX_5Y5Z502YH8C100090";
	private static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNdPsM2rXslYyxJCzkJydHn+9xZiyl1VXli+3Xy6PMop7hhgDnOIL/ZUPsZNIxooladH1vbRqsv24kZIvQrFzxsW/H+Y4GbK9T0DdW39eU2eMwlxrhqqfn5aVdkl6pkhQdrN9HK6OY8Cd1Sx5mIrR/yNU9l0Q9njnwl6ltUQlaGabYfpvqED3OsXvh9ERi0CAtzSbiboR9t5O1Yz308YsR5mMvN9MocL/DbGGbeb3nUJOMPEQ76w3ar7GOUO1n8Hby9rMXUVewnfw4NIobOppnwBu559X26qT1Qz+amPKE2mZJomnuULpZGLt6nEWiKCNk8XO41S49XEIiH3X+91BbAgMBAAECggEAb2IxXAnLPiLzyXgGsQ/zaf6Rd+VQOrQlEr5/5W+/5p4WJNZzV52z6I5AUZ+Bsvuc09DuBBMQ8f06KQXS4dkWxmlE712da4mg9In7STiwc9Cth9EzpYXO+FZnQ6rBRxxzInrAxTWjeAWBzGX95cZmAkiNQBYf8bTpQnzpN96bUom5Jaa4HgCgDsE0ablMpzdkH8JRgkdhzbUPAIUIjCylh3pnGFpvXRZVHMzkhALG5INEOm/kWog3OZNlAczcUGYdmqmZPAe8iGQJ7nftEAts4FPM+qBotPBx5SrcoI5xwnbYf0pTRNiaICKFc1adfd6+4HiFZ33xG9ciaxqFtdp0YQKBgQDdfdQ7yH3zWZz5ImJ/WkGEvYfFGhg62Q8N15pFZNKCK/+STrZCgiicKL3w/PnJShEVRV1r3X5PJb8/YgIsvMH7fuyt2YSgsvf79Cn3UbEsm0iyC4KduZisby7ep1j0u5lYCh6WOQsHRTV+xS4FZg6srvqSi3ngOyYJPNcA0Mwq5QKBgQCjfvrKyTR7E6mZN2U1mkRgTzMjuq4sfOC8Xk+LMyswuSKcegbvIF61wV267aOPYGrOywHa4H7fhNh7bxx1ecd8JNkWTHWKNB2lNjODP//ozy6AaOWXEIcjGYhGdkzwB7fdBpwfWvcqEzWy08+E7idATdQ+V4wJhShH7QRuMY+aPwKBgQCFBBI3LPrm8ERjIhFBJjT+wMwOxNMUVFLEH10Ym6oIXuAHP5oZaw10astXNLXV+tMzkQq/wq8eXS7zo6pmr51Xqiv9ZApQb+qZt1KUosziWwdXVa5FhrZ3p/Xf5WR45Uj8JGemE+/Zl+i+jzBKMbZSYms9IuI4lY0MMytz4/EZOQKBgDFhXrn0v0cVDzpZgtYHu/MiuJ1n+scxCGecTIYMdFUI9a0xJfr/WdunhXm54JajVl8S7YKAH5G+++t8Cappl5ilQHP9r9qIFxQZJMHViKN2P+NEy2Mu+MHfKkut21r72cqF/o1x2gj5OFTEqH7Lt0sytpIFuyP89ceTjK0Dhw3dAoGADyBP3VYZt0LHMesNxkkh+Brx5mzCY1J+6gEk0MYynWpjMyh55NIyLP1YtyqPPx8rBPCHJxY16hOAkuPob4FAj1r3h5x282DgNFxgrHJO9LeUJD3AAWLSIPMyrQQuU/TF783LEda0CkwzIkACIzF0+mlZ6SEOL76uLK8p/pIOzWM=";
	//  Alipay public key 
	private static String alipay_public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjXT7DNq17JWMsSQs5CcnR5/vcWYspdVV5Yvt18ujzKKe4YYA5ziC/2VD7GTSMaKJWnR9b20arL9uJGSL0Kxc8bFvx/mOBmyvU9A3Vt/XlNnjMJca4aqn5+WlXZJeqZIUHazfRyujmPAndUseZiK0f8jVPZdEPZ458JepbVEJWhmm2H6b6hA9zrF74fREYtAgLc0m4m6EfbeTtWM99PGLEeZjLzfTKHC/w2xhm3m951CTjDxEO+sN2q+xjlDtZ/B28vazF1FXsJ38ODSKGzqaZ8AbuefV9uqk9UM/mpjyhNpmSaJp7lC6WRi7epxFoigjZPFzuNUuPVxCIh91/vdQWwIDAQAB";
	private static String notify_url = "http://localhost:9091/alipay/notify"; 
	private static String return_url = "http://localhost:9091/alipay/paySuccess";
	private static String SIGNTYPE = "RSA2";
	private static String CHARSET = "UTF-8";
	private static String gatewayUrl="https://open-na.alipay.com";
	private static String accessTokenLock = "85HLmOH6P3OH4ut6z8nVKhtcIEnT7djCAO2zvdvnZj0002C1";
	//  Request gateway address
//	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
//	//  Returns the format 
//	public static String FORMAT = "json";
////  Log directory
//	public static String log_path = "/log";
//	//Application id
//    //@Value("${alipay.appId}")
//    private String appId;
//
//    //Private key
//    //@Value("${alipay.privateKey}")
//    private String privateKey;
//
//    //Public key
//    //@Value("${alipay.publicKey}")
//    private String publicKey;
//
//    //Alipay gateway
//    //@Value("${alipay.gateway}")
//    private String gateway;
//
//    //The callback address of the interface after successful payment is not a friendly page for callback. Don't confuse it
//    //@Value("${alipay.notifyUrl}")
//    private String notifyUrl;
    
    //Project initialization events
    @Override
    public void run(ApplicationArguments args) {
        //Initializing Alipay SDK
        Factory.setOptions(getOptions());
    }
//    @Bean
//    public Config getAlipayConfig() {
//    	Config config= new Config();
//    	config.merchantPrivateKey= merchantPrivateKey;
//    	config.signType=signType;
//    	config.gatewayHost=gatewayHost;
//    	config.protocol = protocol;
//    	config.appId = appId;
//    	return config;
//    }

	private Config getOptions() {
		//Some unnecessary configurations are omitted here. Please refer to the description in the document
        Config config = new Config();
        config.merchantPrivateKey = merchant_private_key ;
        config.alipayPublicKey = alipay_public_key;
        config.notifyUrl = notify_url;
        config.protocol = "https";
        config.gatewayHost = gatewayUrl;
        config.signType = SIGNTYPE;
        
        config.appId = app_id;
		return config;
	}
	//BasicSessionCredentials basicSessionCredentials = BasicSessionCredentials(app_id,merchant_private_key,accessTokenLock, 15L);

	
}
