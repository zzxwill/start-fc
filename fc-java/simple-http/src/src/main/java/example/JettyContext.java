package example;

import com.aliyun.fc.runtime.*;

public class JettyContext implements Context {
    @Override
    public String getRequestId() {
        return "";
    }

    @Override
    public Credentials getExecutionCredentials() {
        return null;
    }

    @Override
    public FunctionParam getFunctionParam() {
        return null;
    }

    @Override
    public Service getService() {
        return null;
    }

    @Override
    public FunctionComputeLogger getLogger() {
        return null;
    }

    @Override
    public OpenTracing getTracing() {
        return null;
    }

    @Override
    public int getRetryCount() {
        return 0;
    }
    // Implement required Context interface methods
}
