package com.demo.aws.pinpoint.service;

import com.demo.aws.pinpoint.model.CampaignModel;
import com.demo.aws.pinpoint.model.EndPoint;

import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.EndpointRequest;

public interface PinpointService {
	
	public EndpointRequest createEndpointRequestData(EndPoint endpointUser);
	
	public String createCampaign(PinpointClient client, String segmentId, CampaignModel model );
	

}
