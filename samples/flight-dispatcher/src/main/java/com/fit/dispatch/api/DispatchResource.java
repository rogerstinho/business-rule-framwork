package com.fit.dispatch.api;

import com.fit.dispatch.context.DispatchRequest;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/api/dispatch")
public class DispatchResource {

    private final DispatchService dispatchService;

    public DispatchResource(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @POST
    public DispatchResponse dispatch(DispatchRequest request) {
        return dispatchService.dispatch(request);
    }
}
