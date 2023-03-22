package bluemarble;

import java.util.Scanner;

public class Bluemarble {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		GameSetting gs = new GameSetting();
		
		// 게임 생성
		System.out.print("플레이어 수를 입력하세요. (1~4): ");
		int plNum = Integer.parseInt(sc.nextLine());
		while(!(plNum>=0 && plNum<=4)) {
			System.out.print("플레이어 수를 입력하세요. (1~4): ");
			plNum = Integer.parseInt(sc.nextLine());
		}
		Player[] pl = new Player[4];
		for(int i=0; i<4; i++) {
			pl[i] = new Player();
			pl[i].mark = (char)(pl[i].mark + i);
			if(i>plNum-1)
				pl[i].isCom = true;
			if(pl[i].isCom) {
				System.out.println("컴퓨터 플레이어 " + pl[i].mark);
			}
		}
		System.out.println();
		
		printMap(pl, gs);  // 맵 출력
		
		while(true) {  // 게임 시작
			for(int t=0; t<4; ) {  // 턴 시작
				if(gs.isGameClear) {
					break;
				}
				if(pl[t].isGameOver) {
					t++;
					continue;
				}
				pl[t].isTurnOver = false;
				gs.doubCnt = 0;
				System.out.println("플레이어 " + pl[t].mark + " 턴!");
				sc.nextLine();
				if(pl[t].isIsland) {  // 무인도 탈출 판정
					pl[t].islandCnt--;
					if(pl[t].islandCnt==0) {
						pl[t].isIsland = false;
						System.out.println("무인도를 탈출합니다.");
						sc.nextLine();
					} else {
						System.out.println("무인도 탈출까지 " + pl[t].islandCnt + " 턴 남았습니다.");
						System.out.println("탈출을 시도합니다.");
						sc.nextLine();
					}
				}
				while(true) {  // 주사위 던지기
					gs.isPay = false;
					gs.isDoubleDice = false;
					System.out.println("주사위를 던집니다.");
					sc.nextLine();
					int[] dice = new int[2];
					for(int i=0; i<2; i++) {
						dice[i] = (int)(Math.random()*4)+1;
					}
					System.out.println("주사위 결과 : " + dice[0] + ", " + dice[1]);
					if(dice[0]==dice[1]) {
						gs.isDoubleDice = true;
						if(pl[t].isIsland) {
							System.out.println("무인도 탈출을 성공했습니다.");
							pl[t].isIsland = false;
							sc.nextLine();
						}
						gs.doubCnt++;
						System.out.println(gs.doubCnt + " 번째 더블입니다.");
						if(gs.doubCnt==3) {
							System.out.println("무인도로 이동합니다.");
							sc.nextLine();
							pl[t].isIsland = true;
							gs.isDoubleDice = false;
						}
					} else {
						pl[t].isTurnOver = true;
						
						if(pl[t].isIsland) {
						System.out.println("무인도 탈출 실패.");
						sc.nextLine();
						break;
						}
					}
					// 이동 및 맵 출력
					if(pl[t].isIsland) {
						pl[t].moveIsland();
					} else {
						System.out.print(gs.gameMap[pl[t].location] + "에서 출발하여 ");
						pl[t].location += (dice[0] + dice[1]);
						System.out.println(gs.gameMap[pl[t].location%gs.size] + "에 도착합니다.");
						if(pl[t].location>=gs.size) {
							pl[t].location %= gs.size;
							pl[t].money += gs.cyclePrice;
							System.out.println("완주 성공!\n상금 " + gs.cyclePrice + " 획득!");
						}
						sc.nextLine();
					}
					while(true) {
						boolean escape = true;
						switch(pl[t].location) {
						case 0:
							System.out.println(gs.gameMap[0] + " 도착 특별 상금 " + (gs.cyclePrice/2) + " 획득!");
							pl[t].money += gs.cyclePrice/2;
							break;
						case (GameSetting.line-1):
							escape = false;
							System.out.println("미로에 진입하였습니다. 무작위 위치로 이동합니다.");
							pl[t].location = (int)(Math.random()*gs.size);
							sc.nextLine();
							System.out.println(gs.gameMap[pl[t].location] + " 도착.");
							break;
						case (GameSetting.line-1)*2:
							pl[t].isIsland = true;
							pl[t].isTurnOver = true;
							pl[t].islandCnt = 3;
							System.out.println("무인도에 갇혔습니다.");
							break;
						case (GameSetting.line-1)*3:
							System.out.println("공항에 도착하였습니다. 원하는 지역으로 이동합니다.\n");
							sc.nextLine();
							for(int j=0; j<gs.area.length; j++) {
								System.out.print(gs.area[j] + "\t");
							}
							System.out.println();
							for(int j=0; j<gs.area.length; j++) {
								System.out.print("  " + (j+1) + "\t");
							}
							System.out.println();
							int go = 0;
							if(pl[t].isCom) {
								while(true) {
									boolean chk = false;
									go = (int)(Math.random()*gs.area.length);
									for(int j=0; j<gs.size; j++) {
										if(gs.gameMap[j]==gs.area[go]) {
											if(gs.bldMap[j].charAt(0)==pl[t].mark || gs.bldMap[j].charAt(0)==gs.bldMark[0].charAt(0)) {
											chk = true;
											}
											break;
										}
									}
									if(chk)
										break;
								}
							} else {
								while(true) {
									System.out.print("원하는 지역의 번호 입력 : ");
									go = Integer.parseInt(sc.nextLine());
									if(go>=1 && go<=gs.area.length)
										break;
								}
							}
							for(int j=0; j<gs.size; j++) {
								if(gs.gameMap[j]==gs.area[go]) {
									pl[t].location = j;
									System.out.println(gs.gameMap[j] + " 도착.");
								}
							}
							break;
						}
						sc.nextLine();
						if(escape)
							break;
					}
					
					while(true) {  // 이동 후 플레이
						
						printMap(pl, gs);  // 맵 출력
						
						if(pl[t].isIsland)
							break;
						
						gs.isBuy = false;
						if(gs.isPay) {
							gs.isPay = false;
							break;
						}
						for(int j=0; j<gs.area.length; j++) {
							if(gs.gameMap[pl[t].location].equals(gs.area[j])) {
								int chk = 0;
								switch(gs.bldMap[pl[t].location].charAt(0)) {
								case '_':
									if(pl[t].isCom) {
										chk = pl[t].money>100000 ? 1 : ((int)(Math.random()*100)>88 ? 1 : 2);
									} else {
										System.out.println("지역 개발이 가능합니다.");
										System.out.println(gs.gameMap[pl[t].location] + "의 개발비용은 " + gs.priceMap[pl[t].location] + "입니다.");
										System.out.println("지역을 개발하시겠습니까?");
										System.out.println("Yes : 1\t\t No : 2");
										chk = sc.nextInt();
									}
									gs.isBuy = chk==1 ? true : false;
									if(gs.isBuy) {
										if(pl[t].money>=gs.priceMap[pl[t].location]) {
											gs.bldMap[pl[t].location] = "" + pl[t].mark;
											pl[t].money -= gs.priceMap[pl[t].location];
											gs.priceMap[pl[t].location] = (int)(gs.priceMap[pl[t].location]*1.3);
											System.out.println("지역을 개발합니다.");
										} else {
											System.out.println("자금이 부족합니다.");
											gs.isBuy = false;
										}
									}
									break;
								default:
									if(gs.bldMap[pl[t].location].charAt(0)==pl[t].mark) {
										if(gs.bldMap[pl[t].location].length()<4) {
											if(pl[t].isCom) {
												chk = pl[t].money>100000 ? 1 : ((int)(Math.random()*100)>88 ? 1 : 2);
											} else {
												System.out.println("지역 개발이 가능합니다.");
												System.out.println(gs.gameMap[pl[t].location] + "의 개발비용은 " + gs.priceMap[pl[t].location] + "입니다.");
												System.out.println("지역을 개발하시겠습니까?");
												System.out.println("Yes : 1\t\t No : 2");
												chk = sc.nextInt();
											}
											gs.isBuy = chk==1 ? true : false;
											if(gs.isBuy) {
												if(pl[t].money>=gs.priceMap[pl[t].location]) {
													pl[t].money -= gs.priceMap[pl[t].location];
													gs.bldMap[pl[t].location] = gs.bldMap[pl[t].location] + gs.bldMark[1];
													gs.priceMap[pl[t].location] = (int)(gs.priceMap[pl[t].location]*1.3);
													System.out.println("지역을 개발합니다.");
												} else {
													System.out.println("자금이 부족합니다.");
													gs.isBuy = false;
												}
											}
										} else {
											System.out.println("더 이상 개발 할 수 없습니다.");
											sc.nextLine();
										}
									} else {
										gs.isPay = true; 
										int pay = (int)(gs.priceMap[pl[t].location]*1.5);
										System.out.println(gs.bldMap[pl[t].location].charAt(0) + "의 구역입니다.");
										System.out.println("체류비용으로 " + pay + "지불.");
										pl[t].money -= pay;
										for(int owner=0; owner<4; owner++) {
											if(gs.bldMap[pl[t].location].charAt(0)==pl[owner].mark) {
												pl[owner].money += pay;
												break;
											}
										}
										if(pl[t].money<0) {
											System.out.println("자금이 부족합니다.");
											System.out.println(pl[t].mark + " 플레이어 파산.");
											sc.nextLine();
											pl[t].isGameOver = true;
											pl[t].isTurnOver = true;
											for(int pp=0, dd=0; pp<4; pp++) {
												if(pl[pp].isGameOver)
													dd++;
												if(dd==3)
													gs.isGameClear = true;
											}
										}
									}
									break;
								}
								sc.nextLine();
								if(pl[t].isGameOver)
									break;
							}
						}
						
						if(!gs.isBuy && !gs.isPay)
							break;
					}
					
					if(pl[t].isTurnOver) {
						break;
					}
					
					if(gs.isDoubleDice) {
						continue;
					}
				}
				if(pl[t].isTurnOver) {
					System.out.println(pl[t].mark + " 턴 종료.");
					sc.nextLine();
					t++;
					continue;
				}
			}
			if(gs.isGameClear) {
				for(int pp=0; pp<4; pp++) {
					if(!pl[pp].isGameOver) {
						System.out.println(pl[pp].mark + " 플레이어 승!");
					}
				}
				System.out.println("<<<<끝>>>>");
				break;
			}
		}
	}
	
	static void printMap(Player[] pl, GameSetting gs) {
		// 맵 전체 출력
		for(int i=0; i<=GameSetting.line; i++) {
			if(i==0) {
				for(int j=0; j<4; j++) {
					if(pl[j].isGameOver) {
						System.out.print(pl[j].mark + " : Game Over");
					} else {
						System.out.print(pl[j].mark + " 자금 : " + pl[j].money + "\t");
					}
					if(j%2!=0) {
						System.out.println();
					}
				}
				System.out.println();
				continue;
			}
			int z = gs.size;
			if(i==1) {
				z=0;
			} else {
				z -= (i-1);
			}
			
			//플레이어 위치 울력
			for(int j=0; j<GameSetting.line; j++) {
				for(int l=0; l<4; l++) {
					if(pl[l].isGameOver)
						continue;
					if(i==1 && pl[l].location==j) {
						System.out.print(pl[l].mark);
					} else if(i==GameSetting.line && pl[l].location==z-j) {
						System.out.print(pl[l].mark);
					} else {
						if(j==0 && pl[l].location==z) {
							System.out.print(pl[l].mark);
						} else if(j==GameSetting.line-1 && pl[l].location==j+i-1)
							System.out.print(pl[l].mark);
					}
				}
				System.out.print("\t\t");
			}
			System.out.println();
			
			// 멥 출력
			for(int j=0; j<GameSetting.line; j++) {
				if(i==1) {
					System.out.print(gs.gameMap[j]);
				} else if(i==GameSetting.line) {
					System.out.print(gs.gameMap[z-j]);
				} else {
					if(j==GameSetting.line-1)
						System.out.print(gs.gameMap[j+i-1]);
					else if(j==0)
						System.out.print(gs.gameMap[z]);
				}
				System.out.print("\t\t");
			}
			System.out.println();

			// 지역 기본 개발비용 출력
			for(int j=0; j<GameSetting.line; j++) {
				if(i==1 && gs.priceMap[j]>0) {
					System.out.print(gs.priceMap[j]);
				} else if(i==GameSetting.line && gs.priceMap[z-j]>0) {
					System.out.print(gs.priceMap[z-j]);
				} else {
					if(j==GameSetting.line-1 && gs.priceMap[j+i-1]>0)
						System.out.print(gs.priceMap[j+i-1]);
					else if(j==0 && gs.priceMap[z]>0)
						System.out.print(gs.priceMap[z]);
				}
				System.out.print("\t\t");
			}
			System.out.println();
			
			// 지역 개발현황 출력
			for(int j=0; j<GameSetting.line; j++) {
				if(i==1) {
					System.out.print(gs.bldMap[j]);
				} else if(i==GameSetting.line) {
					System.out.print(gs.bldMap[z-j]);
				} else {
					if(j==GameSetting.line-1)
						System.out.print(gs.bldMap[j+i-1]);
					else if(j==0)
						System.out.print(gs.bldMap[z]);
				}
				System.out.print("\t\t");
			}
			System.out.println("\n");
		}
	}
	
}

class Player {

	boolean isCom;
	boolean isGameOver;
	boolean isTurnOver;
	boolean isIsland;
	int money = 500000;
	int islandCnt = 3;
	int location;
	char mark = 'A';
	
	void moveIsland() {
		location = 2*(GameSetting.line-1);
	}
}

class GameSetting {
	int cyclePrice = 30000;
	boolean isBuy;
	boolean isPay;
	boolean isGameClear;
	boolean isDoubleDice;
	int doubCnt = 0;
	final static int line = 6;
	int size = (line-1)*4;
	String[] area = new String[16];
	{
		area[0] = "[서울]";
		area[1] = "[경기]";
		area[2] = "[충북]";
		area[3] = "[충남]";
		area[4] = "[강원]";
		area[5] = "[경북]";
		area[6] = "[경남]";
		area[7] = "[전북]";
		area[8] = "[전남]";
		area[9] = "[제주]";
		area[10] = "[인천]";
		area[11] = "[부산]";
		area[12] = "[광주]";
		area[13] = "[세종]";
		area[14] = "[대전]";
		area[15] = "[울산]";
	}
	String[] corner = new String[4];
	{
		corner[0] = "[시작]";
		corner[1] = "[미로]";
		corner[2] = "[무인도]";
		corner[3] = "[공항]";
	}
	String[] bldMark = new String[2];
	{
		bldMark[0] = "______";
		bldMark[1] = "H";
	}
	String[] gameMap = new String[size];
	int[] priceMap = new int[size];
	String[] bldMap = new String[size];
	{
		for(int i=0; i<4; i++) {
			gameMap[i*(line-1)] = corner[i];
			bldMap[i*(line-1)] = "";
		}
		int[] index = new int[area.length];
		for(int i=0; i<area.length ; ) {
			boolean isOverlap = false;
			index[i] = (int)(Math.random()*size);
			if(index[i]%(line-1)==0)
				continue;
			for(int j=0; j<i; j++) {
				if(index[i]==index[j]) {
					isOverlap = true;
					break;
				}
			}
			if(isOverlap)
				continue;
			gameMap[index[i]] = area[i];
			priceMap[index[i]] = (int)(Math.random()*5+1)*10000; 
			bldMap[index[i]] = bldMark[0];
			i++;
		}
		for(int i=0; i<size; i++) {
			if(gameMap[i]==null) {
				gameMap[i] = "[____]";
				bldMap[i] = "";
			}
		}
	}
}
