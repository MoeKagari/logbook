package logbook.context.dto.battle;

import java.util.ArrayList;

import logbook.config.AppConstants;
import logbook.gui.logic.TimeString;

/**
 * 出击之后到回港之前所有dto的超类
 * @author MoeKagari
 */
public abstract class BattleDto implements HasDownArrow<BattleDto> {

	public static String getSearch(int id) {
		switch (id) {
			case 1:
				return "成功(无损)";
			case 2://有未归还
				return "成功(有损)";
			case 3://全部未归还
				return "成功(全损)";
			case 4:
				return "失败(有)";
			case 5://没有舰载机
				return "成功(无)";
			case 6://没有舰载机
				return "失败(无)";
			default:
				return Integer.toString(id);
		}
	}

	public static String getHangxiang(int id) {
		switch (id) {
			case 1:
				return "同航战";
			case 2:
				return "反航战";
			case 3:
				return "T有利";
			case 4:
				return "T不利";
			default:
				return Integer.toString(id);
		}
	}

	public static String getZhenxin(int id) {
		switch (id) {
			case 1:
				return "单纵阵";
			case 2:
				return "复纵阵";
			case 3:
				return "轮型阵";
			case 4:
				return "梯形阵";
			case 5:
				return "单横阵";
			case 11:
				return "第一警戒航行序列";
			case 12:
				return "第二警戒航行序列";
			case 13:
				return "第三警戒航行序列";
			case 14:
				return "第四警戒航行序列";
			default:
				return Integer.toString(id);
		}
	}

	public static String getSeiku(int id) {
		switch (id) {
			case 0:
				return "制空均衡";
			case 1:
				return "制空确保";
			case 2:
				return "制空优势";
			case 3:
				return "制空劣势";
			case 4:
				return "制空丧失";
			default:
				return Integer.toString(id);
		}
	}

	public static String getNextPointType(int nextEventId, int nextEventKind) {
		switch (nextEventId) {
			case 2:
				switch (nextEventKind) {
					case 0:
						return "资源获得";
				}
				break;
			case 3:
				switch (nextEventKind) {
					case 0:
						return "渦潮";
				}
				break;
			case 4:
				switch (nextEventKind) {
					case 1:
						return "通常战斗";
					case 2:
						return "夜战";
					case 4:
						return "空袭战";
					case 6:
						return "长距离空袭战";
				}
				break;
			case 5:
				switch (nextEventKind) {
					case 1:
						return "BOSS";
					case 5:
						return "联合舰队BOSS";
				}
				break;
			case 6:
				switch (nextEventKind) {
					case 0:
						return "気のせいだった";
					case 1:
						return "敵影を見ず";
					case 2:
						return "能动分歧";
					case 3:
						return "穏やかな海です";
				}
				break;
			case 7:
				switch (nextEventKind) {
					case 0:
						return "航空侦察";
					case 4:
						return "航空战";
				}
				break;
			case 8:
				switch (nextEventKind) {
					case 0:
						return "船团护卫成功";
				}
				break;
			case 9:
				switch (nextEventKind) {
					case 0:
						return "扬陆地点";
				}
				break;
		}

		return nextEventId + "-" + nextEventKind;
	};

	public static String getRank(String rank) {
		switch (rank) {
			case "S":
				return "S胜利";
			case "A":
				return "A胜利";
			case "B":
				return "B战术的胜利";
			case "C":
				return "C战术的败北";
			case "D":
				return "D败北";
			case "E":
				return "E败北";
			default:
				return rank;
		}
	}

	public static String getSupportType(int type) {
		switch (type) {
			case 1:
				return "航空支援";
			case 2:
				return "炮击支援";
			case 3:
				return "雷击支援";
			default:
				return "新支援类型:" + type;
		}
	}

	/*-----------------------------------------------------------------------------*/
	private final long time = TimeString.getCurrentTime();

	public long getTime() {
		return this.time;
	}

	public abstract BattleType getBattleType();

	public boolean isPracticeBattle() {
		return false;
	}

	public boolean isStart() {
		return false;
	}

	/**
	 * 战斗时的舰队的信息
	 * @author MoeKagari
	 */
	public static class BattleDeck {
		public final ArrayList<Integer> escapes = new ArrayList<>();
		public final boolean isCombine;
		public final boolean isEnemy;
		public final int[] nowhps;
		public final int[] maxhps;
		public String[] names = AppConstants.EMPTY_NAMES;

		public BattleDeck(boolean isCombine, boolean isEnemy, int[] nowhps, int[] maxhps) {
			this.isCombine = isCombine;
			this.isEnemy = isEnemy;
			this.nowhps = nowhps;
			this.maxhps = maxhps;
		}

		public boolean exist() {
			for (int nowhp : this.nowhps) {
				if (nowhp != -1) {
					return true;
				}
			}
			return false;
		}

		public int getShipCount() {
			int count = 0;
			for (int i = 0; i < 6; i++) {
				if (this.nowhps[i] != -1) {
					count++;
				}
			}
			return count;
		}

		public void setNames(String[] names) {
			this.names = names;
		}
	}

	/**
	 * 昼战开幕反潜,三次炮击战,夜战
	 * @author MoeKagari
	 */
	public static class BattleOneAttack {
		/** 敌联合舰队时存在(因为有混战)  */
		public final Boolean enemyAttack;
		public final int attackIndex;//攻击方位置(1-12),enemyAttack所代表的两只舰队,非联合舰队时,自方舰队在前,联合舰队时,第一舰队在前
		public final int[] defenseIndexs;//attackIndex的对方
		public final int[] dmgs;//此次造成的伤害,与defenseIndexs长度相同
		public final int attackType;//攻击类型,昼夜战不同
		public final boolean isMidnight;

		/**
		 * 夜战用
		 */
		public BattleOneAttack(int attackIndex, int[] defenseIndexs, int[] dmgs, int attackType) {
			this(null, true, attackIndex, defenseIndexs, dmgs, attackType);
		}

		/**
		 * 昼战用
		 */
		public BattleOneAttack(Boolean enemyAttack, boolean isMidnight, int attackIndex, int[] defenseIndexs, int[] dmgs, int attackType) {
			this.enemyAttack = enemyAttack;
			this.attackIndex = attackIndex;
			this.defenseIndexs = defenseIndexs;
			this.dmgs = dmgs;
			this.attackType = attackType;
			this.isMidnight = isMidnight;
		}
	}

	/**
	 * 接收{@link BattleOneAttack}进行模拟
	 * @author MoeKagari
	 */
	public static class BattleOneAttackSimulator {
		public final int[] fdmg = new int[6];
		public final int[] fatt = new int[6];
		public final int[] edmg = new int[6];
		public final int[] eatt = new int[6];
		public final int[] fdmgco = new int[6];
		public final int[] fattco = new int[6];
		public final int[] edmgco = new int[6];
		public final int[] eattco = new int[6];

		/**
		 * @param fcombine 自方参战deck是否是联合舰队
		 */
		public void accept(BattleOneAttack boa, Boolean fcombine) {
			Boolean enemyAttack = boa.enemyAttack;
			int attackIndex = boa.attackIndex;
			int[] defenseIndexs = boa.defenseIndexs;
			int[] damages = boa.dmgs;

			int[][] atter = null, dmger = null;
			if (enemyAttack == null && fcombine != null) {//敌方非联合舰队
				atter = new int[][] { fcombine == Boolean.TRUE ? this.fattco : this.fatt, this.eatt };
				dmger = new int[][] { fcombine == Boolean.TRUE ? this.fdmgco : this.fdmg, this.edmg };
			} else if (enemyAttack == Boolean.FALSE) {//敌联合舰队,我方攻击
				atter = new int[][] { this.fatt, this.fattco };
				dmger = new int[][] { this.edmg, this.edmgco };
			} else if (enemyAttack == Boolean.TRUE) {//敌联合舰队,敌方攻击
				atter = new int[][] { this.eatt, this.eattco };
				dmger = new int[][] { this.fdmg, this.fdmgco };
			} else {
				System.out.println("enemyAttack == null && fcombine == null");
			}

			for (int i = 0; i < damages.length; i++) {
				if (defenseIndexs[i] == -1) continue;//三炮CI -> [index,-1,-1]

				if (atter != null) atter[(attackIndex - 1) / 6][(attackIndex - 1) % 6] += damages[i];
				if (dmger != null) dmger[(defenseIndexs[i] - 1) / 6][(defenseIndexs[i] - 1) % 6] += damages[i];
			}
		}
	}

	/**
	 * BattleDeck 的attack和damage<br>
	 * 供每个 BattleStage 用
	 * @author MoeKagari
	 */
	public static class BattleDeckAttackDamage {
		public final int[] dmg = new int[6];
		public final int[] attack = new int[6];

		/**
		 * 受到伤害
		 */
		public void getDamage(int[] gd) {
			for (int i = 0; i < 6; i++) {
				this.dmg[i] += gd[i];
			}
		}

		/**
		 * 攻击输出
		 */
		public void setAttack(int[] sa) {
			for (int i = 0; i < 6; i++) {
				this.attack[i] += sa[i];
			}
		}

		public BattleDeckAttackDamage add(BattleDeckAttackDamage next) {
			for (int i = 0; i < 6; i++) {
				this.dmg[i] = this.dmg[i] + next.dmg[i];
				this.attack[i] = this.attack[i] + next.attack[i];
			}
			return this;
		}
	}

}
