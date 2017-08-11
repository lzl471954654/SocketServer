import java.io.*
import java.net.Socket

class ServerThread @Throws(IOException::class)
constructor(internal var socket: Socket) : Thread() {
    internal var reader: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    internal var printWriter: PrintWriter = PrintWriter(OutputStreamWriter(socket.getOutputStream()))
    internal var port = socket.port
    internal var inetAddress = socket.inetAddress
    internal var loop = true
    val END = ServerProtocol.END_FLAG
    var contorller = false
    lateinit var id:String


    override fun run() {
        try {
            server()
        }catch (e:Exception){
            e.printStackTrace()
            LogUtils.logException(this.javaClass.name,""+e.message)
        }
    }

    private fun server(){
        val builder = StringBuilder()
        var line: String? = null
        while (loop) {
            try {
                while (true&&socket.isConnected)
                {
                    line = reader.readLine()
                    if (line==null)
                    {
                        println("null")
                        break
                    }
                    builder.append(line)
                    if(line.endsWith(END))
                        break
                }
                LogUtils.logInfo(javaClass.name,"read OK")
                val data = builder.toString()
                println(data)
                builder.delete(0, builder.length)
                println(data.split("_"))
                when(data.split("_")[0])
                {
                    ServerProtocol.CONNECTED_TO_USER->{
                        val params = data.split("_")
                        if(params.size<3){
                            sendParamsError("Params is not enough")
                        }
                        val userMode = params[1]
                        val id = params[2]
                        when(userMode){
                            ServerProtocol.CONTROL->{
                                if(ServerMain.controlSocketMap.contains(id)) {
                                    val userServer = ServerMain.controlSocketMap.get(id)
                                    exchangeIpAddress(userServer!!)
                                }
                                else
                                {
                                    sendNormalMsg("No such user : $id")
                                }
                            }
                            ServerProtocol.BE_CONTROLLED->{
                                if(ServerMain.beControlledSocketMap.contains(id)){
                                    val userServer = ServerMain.beControlledSocketMap.get(id)
                                    exchangeIpAddress(userServer!!)
                                }
                                else{
                                    sendNormalMsg("No such user : $id")
                                }
                            }
                        }
                    }
                    ServerProtocol.HEATR_BEAT->{
                        printWriter.println(ServerProtocol.HEATR_BEAT+END)
                        printWriter.flush()
                    }
                    ServerProtocol.ONLINE->{
                        println("online")
                        val paramas = data.split("_")
                        if(paramas.size<3)
                        {
                            val error = "Params is not enough"
                            sendParamsError(error)
                            return
                        }
                        val id:String = paramas[1]
                        val mode = paramas[2]
                        println("mode")
                        when(mode){
                            ServerProtocol.CONTROL->{
                                if(ServerMain.controlSocketMap.contains(id))
                                {
                                    sendNormalMsg(ServerProtocol.ONLINE_FAILED)
                                    releaseSocket()
                                }
                                else{
                                    ServerMain.controlSocketMap.put(id,this)
                                    sendNormalMsg(ServerProtocol.ONLINE_SUCCESS)
                                    contorller = true
                                    this.id = id
                                }
                            }
                            ServerProtocol.BE_CONTROLLED->{
                                if(ServerMain.beControlledSocketMap.contains(id)){
                                    sendNormalMsg(ServerProtocol.ONLINE_FAILED)
                                    releaseSocket()
                                }
                                else{
                                    ServerMain.beControlledSocketMap.put(id,this)
                                    sendNormalMsg(ServerProtocol.ONLINE_SUCCESS)
                                    contorller = false
                                    this.id = id
                                }
                            }
                            else->{
                                println("else")
                                sendParamsError("No such mode")
                            }
                        }
                    }
                    ServerProtocol.OFFLINE->{
                        val paramas = data.split("_")
                        if(paramas.size<2)
                        {
                            val error = "Params is not enough"
                            sendParamsError(error)
                            return
                        }
                        val id:String = paramas[1]
                        val mode:String = paramas[2]
                        when(mode){
                            ServerProtocol.CONTROL->{
                                if(ServerMain.controlSocketMap.contains(id))
                                {
                                    sendNormalMsg("OFFLINE SUCCESS")
                                    ServerMain.controlSocketMap.remove(id)
                                    releaseSocket()
                                }
                            }
                            ServerProtocol.BE_CONTROLLED->{
                                if(ServerMain.beControlledSocketMap.contains(id)){
                                    sendNormalMsg("OFFLINE SUCCESS")
                                    ServerMain.beControlledSocketMap.remove(id)
                                    releaseSocket()
                                }
                            }
                            else->{
                                sendParamsError("No such mode")
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                LogUtils.logException(this.javaClass.name,""+e.message)
                releaseSocket()
            }
            finally {
                if(contorller)
                    ServerMain.controlSocketMap.remove(id)
                else
                    ServerMain.beControlledSocketMap.remove(id)
            }

        }
    }

    fun exchangeIpAddress(server:ServerThread){
        val userPort = server.port
        val userPrinter = server.printWriter
        val userAddress = server.inetAddress

        this.printWriter.println(ServerProtocol.MAKE_HOLE+"_${userAddress.hostAddress}_${userPort}_"+END)
        this.printWriter.flush()
        userPrinter.println(ServerProtocol.MAKE_HOLE+"_${this.inetAddress.hostAddress}_${this.port}_"+END)
        userPrinter.flush()
    }

    fun sendParamsError(msg:String){
        printWriter.println(ServerProtocol.ERROR+"_$msg"+"_"+END)
        printWriter.flush()
        releaseSocket()
    }

    fun sendNormalMsg(msg:String){
        printWriter.println(ServerProtocol.NORMAL_MSG+"_"+msg+"_"+END)
        printWriter.flush()
    }

    fun releaseSocket()
    {
        printWriter.close()
        socket.close()
        loop = false
    }
}
