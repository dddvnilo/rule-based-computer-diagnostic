package com.ftn.sbnz.service;

import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

	@Bean
	public KieContainer kieContainer() {
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();

		KieModuleModel kieModuleModel = ks.newKieModuleModel();
		KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel("DiagnosticsKBase").setDefault(true);
		kieBaseModel.newKieSessionModel("DiagnosticsKSession").setDefault(true);
		kfs.writeKModuleXML(kieModuleModel.toXML());

		kfs.write("src/main/resources/rules/nivo1.drl",
				ks.getResources().newClassPathResource("rules/nivo1.drl"));
		kfs.write("src/main/resources/rules/nivo2-manual.drl",
				ks.getResources().newClassPathResource("rules/nivo2-manual.drl"));
		kfs.write("src/main/resources/rules/nivo3-manual.drl",
				ks.getResources().newClassPathResource("rules/nivo3-manual.drl"));

		generateFromTemplate(kfs, ks, "/rules/nivo2-kvar.drt", Nivo2TemplateData.getRows(), "nivo2-generated.drl");
		generateFromTemplate(kfs, ks, "/rules/nivo3-dijagnoza.drt", Nivo3TemplateData.getRows(), "nivo3-generated.drl");

		KieBuilder kb = ks.newKieBuilder(kfs);
		kb.buildAll();

		if (kb.getResults().hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
			throw new RuntimeException("Greška pri kompajliranju Drools pravila: " + kb.getResults());
		}

		return ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
	}

	private void generateFromTemplate(KieFileSystem kfs, KieServices ks, String templatePath,
			List<Map<String, Object>> data, String outputName) {
		try (InputStream templateStream = ServiceApplication.class.getResourceAsStream(templatePath)) {
			if (templateStream == null) {
				throw new RuntimeException("Template fajl " + templatePath + " nije pronadjen na classpath-u");
			}
			String generatedDrl = new ObjectDataCompiler().compile(data, templateStream);
			long ruleCount = generatedDrl.lines().filter(l -> l.trim().startsWith("rule ")).count();
			System.out.println("[Drools] " + outputName.replace("-generated.drl", "") + ": generisano " + ruleCount + " pravila iz templeta");
			kfs.write("src/main/resources/rules/" + outputName,
					ks.getResources().newByteArrayResource(generatedDrl.getBytes(StandardCharsets.UTF_8)));
		} catch (IOException e) {
			throw new RuntimeException("Greška pri generisanju pravila iz " + templatePath, e);
		}
	}
}
