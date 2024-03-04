package com.ehizman.mock_gloserver.models;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class RequeryApiRequest implements Serializable {
    private String transId;
    private String sponsorId;
}
