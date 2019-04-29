##Learn Transaction Flow
HOST->ZW: 01 05 00 | 50 01 01 | aa <br>
cmd: SET_LEARN_MODE, funcId: 01, mode: LEARN_MODE_CLASSIC

ZW->HOST: 01 07 00 | 50 01 01 01 00 | a9 <br>
cmd: SET_LEARN_MODE, funcId: 01, status: LEARN_STATUS_STARTED, srcId: 01, cmdLen: 00

ZW->HOST: 01 06 00 | 49 10 01 00 | a0 <br>
cmd: APPLICATION_UPDATE, uptStatus: APP_UPDATE_STATUS_SUC_ID, sucID: 01, cmdLen: 00

ZW->HOST: 01 07 00 | 50 01 06 02 00 | ad <br>
(cmd: SET_LEARN_MODE, funcId: 01, status: LEARN_STATUS_DONE, assignedId: 02, cmdLen: 00)
