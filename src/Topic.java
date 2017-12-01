

import java.util.ArrayList;

public class Topic {
	//MQTT通信におけるTopic部
	//このクラスにコードを含めたPubSubの内容を入れる
	String topic;
	String pubsub;
	String value;
	String what;
	Integer QoS;
	String ui;

	//Arduinoのコード部分
	ArrayList<String> log = new ArrayList<String>();//Arduinoのコード本体
	ArrayList<String> pos = new ArrayList<String>();//logを挿入する位置
	ArrayList<String> att = new ArrayList<String>();//logの属性
	ArrayList<String> ins = new ArrayList<String>();//logのを挿入する位置、同じpos内での順位付けに使う


	//setter,getter
	public String getUi() {
		return ui;
	}

	public String getLog(int i){
		return log.get(i);
	}
	public String getPos(int i){
		return pos.get(i);
	}
	public String getAtt(int i){
		return att.get(i);
	}
	public void setUi(String ui) {
		this.ui = ui;
	}
	public void setWhat(String what){
		this.what=what;
	}
	public String getWhat(){
		return what;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getPubsub() {
		return pubsub;
	}

	public void setPubsub(String pubsub) {
		this.pubsub = pubsub;
	}

	public Integer getQoS() {
		return QoS;
	}

	public void setQoS(Integer qoS) {
		QoS = qoS;
	}


	public int getLogsize() {
		return log.size();
	}


	//ArduinoのコードをTopicに保存する時につかう
	public void addLog(String log, String pos, String att) {
		this.log.add(log);
		this.pos.add(pos);
		this.att.add(att);
		this.ins.add("");
	}

	//ArduinoのコードをTopicに保存する時につかう
	//insがある場合は同じpos内でコードが記述される順番が変動する
	//ins:start->ins->end
	public void addLog(String log, String pos, String att,String ins) {
		this.log.add(log);
		this.pos.add(pos);
		this.att.add(att);
		this.ins.add(ins);
	}

	public void addLog(int i,String log, String pos, String att) {
		this.log.add(i,log);
		this.pos.add(i,pos);
		this.att.add(i,att);
		this.ins.add(i,"");
	}


	//Logを全て削除
	public void clearLog() {
		log.clear();
		pos.clear();
		att.clear();
		ins.clear();
	}

	//i番目のログを削除
	public void removeLog(int i) {
		log.remove(i);
		pos.remove(i);
		att.remove(i);
		ins.remove(i);
	}

	//attで指定された属性のログを削除
	public void removeLogAtt(String att) {
		for (int i = 0; i < this.att.size(); i++) {
			if (this.att.get(i).equals(att)) {
				removeLog(i);
				i--;
			}
		}
	}

	//指定したposが最初から何番目にあるかを出力
	public int findPos(String pos){
		for (int i = 0; i < this.pos.size(); i++) {
			if (this.pos.get(i).equals(pos)) {
				return i;
			}
		}
		return -1;
	}


	//topicにたまったログの順番を整理する
	//主にattの属性がある場合、start->ins->endの順番に直す
	public void makeup(String pos){
		ArrayList<String> templog = new ArrayList<String>();
		ArrayList<String> temppos = new ArrayList<String>();
		ArrayList<String> tempatt = new ArrayList<String>();
		ArrayList<String> tempins = new ArrayList<String>();
		for(int i=0;i<log.size();i++){
			if(this.pos.get(i).equals(pos)){
				if(ins.get(i).equals("start")){
					templog.add(log.get(i));
					temppos.add(this.pos.get(i));
					tempatt.add(att.get(i));
					tempins.add(ins.get(i));
					removeLog(i);
					i--;
				}

			}
		}
		for(int i=0;i<log.size();i++){
			if(this.pos.get(i).equals(pos)){
				if(ins.get(i).equals("ins")){
					templog.add(log.get(i));
					temppos.add(this.pos.get(i));
					tempatt.add(att.get(i));
					tempins.add(ins.get(i));
					removeLog(i);
					i--;
				}

			}
		}
		for(int i=0;i<log.size();i++){
			if(this.pos.get(i).equals(pos)){
				if(ins.get(i).equals("end")){
					templog.add(log.get(i));
					temppos.add(this.pos.get(i));
					tempatt.add(att.get(i));
					tempins.add(ins.get(i));
					removeLog(i);
					i--;
				}

			}
		}
		for(int i=0;i<templog.size();i++){
			log.add(templog.get(i));
			this.pos.add(temppos.get(i));
			att.add(tempatt.get(i));
			ins.add(tempins.get(i));
		}
	}

}
