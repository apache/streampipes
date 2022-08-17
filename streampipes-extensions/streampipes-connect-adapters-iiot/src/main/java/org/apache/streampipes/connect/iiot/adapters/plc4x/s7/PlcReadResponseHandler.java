package org.apache.streampipes.connect.iiot.adapters.plc4x.s7;

import org.apache.plc4x.java.api.messages.PlcReadResponse;

public interface PlcReadResponseHandler {

  void onReadResult(PlcReadResponse response,
                    Throwable throwable);
}
