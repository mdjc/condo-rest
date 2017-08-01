package com.github.mdjc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.mdjc.domain.BillHelper;
import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.OutlayHelper;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.UserRepository;
import com.github.mdjc.impl.DefaultBillHelper;
import com.github.mdjc.impl.DefaultOutlayHelper;
import com.github.mdjc.impl.JdbcBillRepository;
import com.github.mdjc.impl.JdbcCondoRepository;
import com.github.mdjc.impl.JdbcOutlayRepository;
import com.github.mdjc.impl.JdbcUserRepository;

@Configuration
public class BeansConfig {
	@Value("${app.bills.payment.images.directory}")
	String billsProofOfPaymentDir;
	
	@Value("${app.outlays.receipt.images.directory}")
	String outlaysReceiptImagesDir;
	
	@Bean
	public UserRepository userRepository(JdbcTemplate template){
		return new JdbcUserRepository(template);
	}
	
	@Bean
	public CondoRepository condoRepository(JdbcTemplate template) {
		return new JdbcCondoRepository(template);
	}
	
	@Bean
	public BillRepository billRepository(NamedParameterJdbcTemplate template) {
		return new JdbcBillRepository(template);
	}
	
	@Bean
	public OutlayRepository outlayRepository(NamedParameterJdbcTemplate template){
		return new JdbcOutlayRepository(template);
	}
	
	@Bean
	public BillHelper billHelper(BillRepository billRepository, CondoRepository condoRepository) {
		return new DefaultBillHelper(billsProofOfPaymentDir, billRepository, condoRepository );
	}
	
	@Bean
	public OutlayHelper outlayHelper() {
		return new DefaultOutlayHelper(outlaysReceiptImagesDir);
	}
}
