package com.demo.aws.pinpoint.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.demo.aws.pinpoint.model.CampaignModel;
import com.demo.aws.pinpoint.model.EndPoint;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.Action;
import software.amazon.awssdk.services.pinpoint.model.CreateCampaignRequest;
import software.amazon.awssdk.services.pinpoint.model.CreateCampaignResponse;
import software.amazon.awssdk.services.pinpoint.model.EndpointDemographic;
import software.amazon.awssdk.services.pinpoint.model.EndpointRequest;
import software.amazon.awssdk.services.pinpoint.model.EndpointUser;
import software.amazon.awssdk.services.pinpoint.model.Message;
import software.amazon.awssdk.services.pinpoint.model.MessageConfiguration;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
import software.amazon.awssdk.services.pinpoint.model.Schedule;
import software.amazon.awssdk.services.pinpoint.model.WriteCampaignRequest;

@Service
public class PinpointServiceImpl implements PinpointService{
	
	@Value("${aws.pinpoint.projectId}")
	private String applicationId;
	
	@Override
	public EndpointRequest createEndpointRequestData(EndPoint endpointUser) {

        HashMap<String, List<String>> customAttributes = new HashMap<>();
        List<String> favoriteTeams = new ArrayList<>();
        favoriteTeams.add("Lakers");
        favoriteTeams.add("Warriors");
        customAttributes.put("team", favoriteTeams);

        EndpointDemographic demographic = EndpointDemographic.builder()
                .appVersion("1.0")
                .make(endpointUser.getMake())
                .model(endpointUser.getModel())
                .modelVersion(endpointUser.getModelVersion())
                .platform(endpointUser.getPlatform())
                .platformVersion(endpointUser.getPlatformVersion())
                .timezone("America/Los_Angeles")
                .build();


        Map<String,Double> metrics = new HashMap<>();
        metrics.put("health", 100.00);
        metrics.put("luck", 75.00);

        
        EndpointUser user = EndpointUser.builder()
                .userId(endpointUser.getUserId())
                .build();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        String nowAsISO = df.format(new Date());
        
        return EndpointRequest.builder()
                .address(endpointUser.getToken())
                .attributes(customAttributes)
                .channelType(endpointUser.getChannelType())
                .demographic(demographic)
                .effectiveDate(nowAsISO)
                .metrics(metrics)
                .optOut("NONE")
                .requestId(UUID.randomUUID().toString())
                .user(user)
                .build();

    }

	@Override
	public String createCampaign(PinpointClient client, String segmentId, CampaignModel model) {
		try {
            Schedule schedule = Schedule.builder()
                .startTime("IMMEDIATE")
                .build();
            
            //Android doesnot work for Standard Message in Background so to fix background issue, raw json is sent
            String rawMessageForAndroid = "{'notification': {'title': '"+model.getTitle()+"','body':'"+model.getBody()+"'}}";

            JsonObject jsonObject = (JsonObject) JsonParser.parseString(rawMessageForAndroid);
           
            Message gcmMessage = Message.builder()
                .action(Action.OPEN_APP)
                .rawContent(jsonObject.toString())
                .build();
            
            Message apnsMessage = Message.builder()
            		.action(Action.OPEN_APP)
            		.body(model.getBody())
            		.title(model.getTitle()).build();

            MessageConfiguration messageConfiguration = MessageConfiguration.builder()
            		.apnsMessage(apnsMessage)
            		.gcmMessage(gcmMessage)
            		.build();

            WriteCampaignRequest request = WriteCampaignRequest.builder()
                .description("My description")
                .schedule(schedule)
                .name(model.getCampaignName())
                .segmentId(segmentId)
                .messageConfiguration(messageConfiguration)
                .build();

            CreateCampaignResponse result = client.createCampaign(CreateCampaignRequest.builder()
                            .applicationId(applicationId)
                            .writeCampaignRequest(request).build()
            );

            System.out.println("Campaign ID: " + result.campaignResponse().id());
         // Uses the Google Gson library to pretty print the endpoint JSON.
            Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

            String campaignResponse = gson.toJson(result.campaignResponse());
            System.out.println(campaignResponse);
            return campaignResponse;

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
	}
	
	

}
