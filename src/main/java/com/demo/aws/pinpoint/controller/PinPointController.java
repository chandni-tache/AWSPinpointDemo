package com.demo.aws.pinpoint.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.aws.pinpoint.model.CampaignModel;
import com.demo.aws.pinpoint.service.PinpointService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.swagger.annotations.Api;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.AttributeDimension;
import software.amazon.awssdk.services.pinpoint.model.AttributeType;
import software.amazon.awssdk.services.pinpoint.model.CreateSegmentRequest;
import software.amazon.awssdk.services.pinpoint.model.CreateSegmentResponse;
import software.amazon.awssdk.services.pinpoint.model.EndpointRequest;
import software.amazon.awssdk.services.pinpoint.model.EndpointResponse;
import software.amazon.awssdk.services.pinpoint.model.GetEndpointRequest;
import software.amazon.awssdk.services.pinpoint.model.GetEndpointResponse;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
import software.amazon.awssdk.services.pinpoint.model.RecencyDimension;
import software.amazon.awssdk.services.pinpoint.model.SegmentBehaviors;
import software.amazon.awssdk.services.pinpoint.model.SegmentDemographics;
import software.amazon.awssdk.services.pinpoint.model.SegmentDimensions;
import software.amazon.awssdk.services.pinpoint.model.SegmentLocation;
import software.amazon.awssdk.services.pinpoint.model.UpdateEndpointRequest;
import software.amazon.awssdk.services.pinpoint.model.UpdateEndpointResponse;
import software.amazon.awssdk.services.pinpoint.model.WriteSegmentRequest;


@RestController
@RequestMapping("/api")
@Api(value = "API to connect with AWS pinpoint",
description = "This API provides the capability to connect with Pinpoint for push notification", produces = "application/json")
public class PinPointController {
	
	@Value("${aws.pinpoint.projectId}")
	private String applicationId;
	
	@Autowired
	public PinpointService pinpointService;
	
	@PostMapping("/createEndPoint")
	public String createEndPoint(@RequestBody com.demo.aws.pinpoint.model.EndPoint endpointUser) {
		
		PinpointClient pinpoint = PinpointClient.builder()
	            .region(Region.AP_SOUTH_1)
	            .build();
				
		 String endpointId = UUID.randomUUID().toString();
	        System.out.println("Endpoint ID: " + endpointId);

	        EndpointRequest endpointRequest = pinpointService.createEndpointRequestData(endpointUser);

	        UpdateEndpointRequest updateEndpointRequest = UpdateEndpointRequest.builder()
	        		.applicationId(applicationId)
	                .endpointRequest(endpointRequest)
	                .endpointId(endpointId)
	                .build();

	        UpdateEndpointResponse updateEndpointResponse = pinpoint.updateEndpoint(updateEndpointRequest);
	        System.out.println("Update Endpoint Response: " + updateEndpointResponse.messageBody().message());
	        
	        GetEndpointRequest appRequest = GetEndpointRequest.builder()
	                .applicationId(applicationId)
	                .endpointId(endpointId)
	                .build();

	            GetEndpointResponse result = pinpoint.getEndpoint(appRequest);
	            EndpointResponse endResponse = result.endpointResponse();
	            
	         // Uses the Google Gson library to pretty print the endpoint JSON.
	            Gson gson = new GsonBuilder()
	                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
	                .setPrettyPrinting()
	                .create();

	            String endpointJson = gson.toJson(endResponse);
	            System.out.println(endpointJson);
	        
	        pinpoint.close();
	        
	        return endpointJson;
	}
	
	public String createSegment() {
		try {
			
			PinpointClient client = PinpointClient.builder()
		            .region(Region.AP_SOUTH_1)
		            .build();
            Map<String, AttributeDimension> segmentAttributes = new HashMap<>();
            segmentAttributes.put("Team", AttributeDimension.builder()
                .attributeType(AttributeType.INCLUSIVE)
                .values("Lakers")
                .build());

            RecencyDimension recencyDimension = RecencyDimension.builder()
                .duration("DAY_30")
                .recencyType("ACTIVE")
                .build();

            SegmentBehaviors segmentBehaviors = SegmentBehaviors.builder()
                .recency(recencyDimension)
                .build();

            SegmentDemographics segmentDemographics = SegmentDemographics
                .builder()
                .build();

            SegmentLocation segmentLocation = SegmentLocation
                .builder()
                .build();

            SegmentDimensions dimensions = SegmentDimensions
                .builder()
               // .attributes(segmentAttributes)
                .behavior(segmentBehaviors)
                .demographic(segmentDemographics)
                .location(segmentLocation)
                .build();

            WriteSegmentRequest writeSegmentRequest = WriteSegmentRequest.builder()
                .name("MySegment1")
                .dimensions(dimensions)
                .build();

            CreateSegmentRequest createSegmentRequest = CreateSegmentRequest.builder()
                .applicationId(applicationId)
                .writeSegmentRequest(writeSegmentRequest)
                .build();

            CreateSegmentResponse createSegmentResult = client.createSegment(createSegmentRequest);
            System.out.println("Segment ID: " + createSegmentResult.segmentResponse().id());
            System.out.println(createSegmentResult.segmentResponse());
            
            Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

            String response = gson.toJson(createSegmentResult.segmentResponse());
            return response;

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
	}
	
	@PostMapping("/push")
	public String createCampaign(@RequestBody CampaignModel model) {
		PinpointClient pinpoint = PinpointClient.builder()
	            .region(Region.AP_SOUTH_1)
	            .build();
		String segmentId = "1c31ed063c3640f48d393f0efdc67f3c";
		return pinpointService.createCampaign(pinpoint,segmentId, model);
		
	}
	
}
