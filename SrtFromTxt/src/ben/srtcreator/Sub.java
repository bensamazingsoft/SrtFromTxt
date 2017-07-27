package ben.srtcreator;

public class Sub {

	private static int idSub = 0;

	public static void setIdSub(int idSub) {
		Sub.idSub = idSub;
	}

	private String Tc = "";
	private String subTxt = "";
	private String id = "";

	public Sub(String str1) {

		this.Tc = makeTC((idSub * 3));
		this.subTxt = str1;
		this.id = String.valueOf(idSub + 1);
		idSub++;
	}

	public Sub(String str1, String str2) {

		this.Tc = makeTC((idSub * 3));
		this.subTxt = str1 + "\n" + str2;
		this.id = String.valueOf(idSub + 1);
		idSub++;
	}

	@Override
	public String toString() {

		String str = this.id + "\n" + this.Tc + "\n" + this.subTxt + "\n\n";
		return str;
	}

	private String makeTC(int time) {

		Timecode tcIn = new Timecode(time);
		Timecode tcOut = new Timecode(time + 2);

		String str = tcIn.getTc() + ",000" + " --> " + tcOut.getTc() + ",500";
		return str;
	}

	class Timecode {

		private String tc = "";

		public Timecode(int time) {
			int HH = 0;
			int MM = 0;
			int SS = 0;
			SS = time;

			while (SS > 60) {
				SS -= 60;
				MM += 1;
			}

			while (MM > 60) {
				MM -= 60;
				HH += 1;
			}

			String hh = String.format("%02d", Integer.valueOf(HH));
			String mm = String.format("%02d", Integer.valueOf(MM));
			String ss = String.format("%02d", Integer.valueOf(SS));

			this.tc = hh + ":" + mm + ":" + ss;
		}

		public String getTc() {
			return this.tc;
		}
	}
}
