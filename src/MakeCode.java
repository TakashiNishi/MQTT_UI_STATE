

import java.util.ArrayList;

//TopicクラスやConnectクラスの情報を基に.inoファイルに入力するコードを決めるクラス
public class MakeCode {

	ArrayList<String> log = new ArrayList<String>();
	ArrayList<String> pos = new ArrayList<String>();
	ArrayList<String> att = new ArrayList<String>();

	Connection con;
	ArrayList<Topic> topic;

	int getCodeSize() {//logのサイズを取得
		return log.size();
	}

	String getCode(int i) {//i番目のlogを取得
		return log.get(i);
	}

	void setLog(String log, String pos, String att) {//新しいLogを追加する
		this.log.add(log);
		this.pos.add(log);
		this.att.add(log);
	}


	//コードを生成する
	MakeCode(Connection con, ArrayList<stateMain> state) {

		int num = state.size();

		ArrayList<Topic> topic=new ArrayList<Topic>();

		for(int i=0;i<state.size();i++){
			topic.add(state.get(i).topic);
		}

		this.con = con;
		this.topic = topic;

		log.clear();
		pos.clear();
		att.clear();


		//setLogにてコード一行を追加する。
		//insertにてTopicやConnectionクラスが保持しているコードを挿入する
		setLog("#include <PubSubClient.h>", "inc", "common");
		insert("inc");
		setLog("", "blank", "common");

		setLog("#define UI_WIFI_SSID \"" + Connection.getWifi_ssid() + "\"", "def", "common");
		setLog("#define UI_WIFI_PASSWORD \"" + String.valueOf(Connection.getWifi_password()) + "\"", "def", "common");
		setLog("#define UI_MQTT_CLIENTID \"" + Connection.getMqtt_clientid() + "\"", "def", "common");
		setLog("#define UI_MQTT_SERVER \"" + Connection.getMqtt_server() + "\"", "def", "common");
		setLog("#define UI_MQTT_PASSWORD \"" + String.valueOf(Connection.getMqtt_password()) + "\"", "def", "common");
		setLog("#define UI_MQTT_PORT \"" + Connection.getMqtt_port() + "\"", "def", "common");
		setLog("#define UI_MQTT_USERNAME \"" + Connection.getMqtt_username() + "\"", "def", "common");
		setLog("", "blank", "common");

		setLog("long ms=0;", "def", "common");
		setLog("void callback(char* topic, byte* payload, unsigned int length);", "def", "common");
		insert("def");
		setLog("", "blank", "common");

		setLog("PubSubClient client(UI_MQTT_SERVER, UI_MQTT_PORT, callback, wifi_client);", "def", "common");
		setLog("long timecounter;", "def", "common");
		setLog("", "blank", "common");

		setLog("void setup(){", "etup", "common");
		setLog("	int state = 0;", "setup", "common");
		setLog("	Serial.begin(115200);", "setup", "common");
		setLog("	Serial.println(\"start\");", "setup", "common");
		setLog("	timecounter = 0;", "setup", "common");
		insert("setup1");
		setLog("	Serial.println(\"WiFi Connected\");", "setup", "common");
		setLog("	while (!client.connected()) {", "setup", "common");
		setLog("		Serial.println(\"connectiong...\");", "setup", "common");
		setLog("    	client.connect(UI_MQTT_CLIENTID, UI_MQTT_USERNAME, UI_MQTT_PASSWORD);", "setup", "common");
		setLog("		delay(1100);", "setup", "common");
		setLog("	}", "setup", "common");
		setLog("	Serial.println(\"Broker Connected\");", "setup", "common");
		insert("setup2");
		setLog("}", "setup", "common");
		setLog("", "blank", "common");

		setLog("void loop(){", "loop", "common");
		setLog("	if (state=="+num+") {", "loop", "common");
		setLog("			state=0;", "loop","common");
		setLog("	}", "loop", "common");
		setLog("	if (!client.connected()) {", "loop", "common");
		setLog("			client.connect(\"UI_MQTT_CLIENTID\", \"UI_MQTT_USERNAME\", \"UI_MQTT_PASSWORD\");", "loop",
				"common");
		setLog("	}", "loop", "common");
		setLog("	timecounter = timecounter+10;", "loop", "common");
		insert("pub");
		setLog("ms = micros();", "loop", "common");
		setLog("}", "loop", "common");
		setLog("", "blank", "common");

		setLog("void callback(char* topic, byte* payload, unsigned int length) {", "callback", "common");
		setLog("	char* json = (char*)malloc(length+1);", "callback", "common");
		setLog("	memcpy(json, payload, length+1);", "callback", "common");
		setLog("	json[length] = \'\\0\';", "callback", "common");
		setLog("	String str = json;", "callback", "common");
		insert("sub");
		setLog("}", "callback", "common");
	}


	//MakeCode関数にてTopicやConnectionクラスに保存されたLogを挿入する
	//Posの優先順位、Topic[0]>Topic[1]>....>Topic[n]>Connecttion
	void insert(String pos) {
		for (int i = 0; i < topic.size(); i++) {
			for (int j = 0; j < topic.get(i).getLogsize(); j++) {
				if (topic.get(i).getPos(j).equals(pos)) {
					setLog(topic.get(i).getLog(j), topic.get(i).getPos(j), topic.get(i).getAtt(j));
				}
			}
		}
		for (int j = 0; j < con.getLogsize(); j++) {
			if (con.getPos(j).equals(pos)) {
				setLog(con.getLog(j), con.getPos(j), con.getAtt(j));
			}
		}
	}

}
