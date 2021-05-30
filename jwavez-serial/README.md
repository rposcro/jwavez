#### General Assumptions
Assumptions based on official protocol specification, assumed to have no more than two frames in 
inbound buffer at the same time as:
 * Dongle won't send next frame following Response or Callback unless it receives
 ACK, NAK, CAN, or timeout occurs.
 * Dongle can send single Response or Callback frame following prior ACK, NAK or CAN.
 * In any other case, buffer is purged and CAN is sent by the application.  

#### Possible Application States
| Application State | Possible Following States
|-------------------|---------------------------
| Idle              | _\<Any\>_ 
| AwaitingACK       | Idle, AwaitingResponse
| AwaitingResponse  | Idle  

#### Supported Inbound Buffer Content Scenarios
Supported application states don't necessarily mean successful scenarios.
They just mean scenarios handled in a way by this library. Others are treated as odd
cases to be handled as exceptional.

| Buffer Content | Supported App State(s) Scenarios 
|----------------|------------------------------------------------------
| _\<empty\>_    | Idle, AwaitingACK or AwaitingResponse
| ACK            | AwaitingACK
| ACK, Response  | AwaitingACK
| ACK, Callback  | AwaitingACK
| NAK            | AwaitingACK  
| NAK, Callback  | AwaitingACK
| CAN            | AwaitingACK  
| CAN, Callback  | AwaitingACK
| Response       | AwaitingACK, AwaitingResponse
| Callback       | AwaitingACK, AwaitingResponse  

#### Actions Taken Upon Scenario
| Application State | Next Buffer Content | Action(s)
|-------------------|---------------------|---------------------------------------------------------
| Idle              | Callback            | send ACK, handle callback, set to Idle
| Idle              | _\<other\>_         | exception(send CAN, empty buffer), set to Idle
| AwaitingACK       | ACK                 | set to Idle or AwaitingResponse 
| AwaitingACK       | NAK                 | consider retransmission
| AwaitingACK       | CAN                 | consider retransmission(?)
| AwaitingACK       | _\<other\>_         | exception(send CAN, empty buffer), consider retransmission
| AwaitingResponse  | ConvergentResponse  | send ACK, handle response, set to Idle
| AwaitingResponse  | DivergentResponse   | exception(send CAN, empty buffer), consider retransmission
| AwaitingResponse  | _\<other\>_         | exception(send CAN, empty buffer), set to Idle


