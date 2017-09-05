package com.github.mdjc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.mdjc.domain.ApartmentRepository;
import com.github.mdjc.domain.BillHelper;
import com.github.mdjc.domain.BillRepository;
import com.github.mdjc.domain.CondoHelper;
import com.github.mdjc.domain.CondoRepository;
import com.github.mdjc.domain.OutlayHelper;
import com.github.mdjc.domain.OutlayRepository;
import com.github.mdjc.domain.UserRepository;
import com.github.mdjc.impl.DefaultBillHelper;
import com.github.mdjc.impl.DefaultCondoHelper;
import com.github.mdjc.impl.DefaultOutlayHelper;
import com.github.mdjc.impl.JdbcApartmentRepository;
import com.github.mdjc.impl.JdbcBillRepository;
import com.github.mdjc.impl.JdbcCondoRepository;
import com.github.mdjc.impl.JdbcOutlayRepository;
import com.github.mdjc.impl.JdbcUserRepository;

@Configuration
public class BeansConfig {
	@Value("${app.condos.images.directory}")
	String condoImagesDir;
	
	@Value("${app.bills.payment.images.directory}")
	String billProofOfPaymentDir;
	
	@Value("${app.outlays.receipt.images.directory}")
	String outlayReceiptImagesDir;
	
	@Bean
	public UserRepository userRepository(NamedParameterJdbcTemplate template){
		return new JdbcUserRepository(template);
	}
	
	@Bean
	public CondoRepository condoRepository(NamedParameterJdbcTemplate template) {
		return new JdbcCondoRepository(template);
	}
	
	@Bean
	public ApartmentRepository apartmentRepository(NamedParameterJdbcTemplate template) {
		return new JdbcApartmentRepository(template);
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
	public CondoHelper condoHelper() {
		return new DefaultCondoHelper(condoImagesDir);
	}
	
	@Bean
	public BillHelper billHelper(BillRepository billRepository, CondoRepository condoRepository) {
		return new DefaultBillHelper(billProofOfPaymentDir, billRepository, condoRepository );
	}
	
	@Bean
	public OutlayHelper outlayHelper(OutlayRepository outlayRepository, CondoRepository condoRepository) {
		return new DefaultOutlayHelper(outlayReceiptImagesDir, outlayRepository, condoRepository);
	}
}
