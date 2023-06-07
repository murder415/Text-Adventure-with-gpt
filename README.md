# 텍스트 어드벤처 With Chat GPT
- Chat GPT 무료 api 키를 이용하여, 누구든 이용할 수 있도록 작성된 Text Adventure Game.
- 소프트웨어학과 정재헌

## 배경
- 한국에 상용화되어있는 Chat GPT를 이용한 게임도 거의 없고, 기본적인 텍스트 어드벤처 게임이 상용화되어있지 않음.
- 해외에서 대표적으로 "AI Dungeon2" 이라는 게임이 있었고, 텍스트 어드벤처 게임에서 선두주자를 달리고 있음.

### 목표
1. 유료 버전이 아닌 무료 API 키 이용 가능 달러가 5달러 또는 18달러로 나와았는데, 이는 최소 20번 이상의 게임을 진행할 수 있습니다. 그리하여 무료 API 키를 이용하여 해당 애플리케이션을 이용할 수 있도록 하였습니다.
2. 기존의 어느 텍스트 어드벤처 게임이든, 아무리 뛰어난 이야기더라도 선택지에서는 정해져있기에 수동적인 느낌을 많이 주고 있습니다. 그리하여 고정된 이야기보다 사용자들이 직접 선택지를 입력할 수 있도록 하였습니다.
3. 글을 읽을 때 글자만 있으면 전달력이 떨어질 수 있기에, 그림을 삽입하는데 이를 착안하여 텍스트 어드벤처 게임 스토리를 진행할 때 현재 진행중인 스토리에 맞게 이미지를 생성할 수 있도록 하였습니다.
4. 수동적인 느낌을 최소화하기 위해 직접 게임에서의 등장인물과 대화하는 느낌을 주고 싶었습니다. 그리하여 게임 진행중 만나는 등장인물에게 말을 걸려고 하였으나, 그리 하기에는 프롬포트의 한계가 있어, 원활한 진행을 못하기에 만나지 않더라도 사용자의 캐릭터와 관련있는 사람들과 대화할 수 있또록 하였습니다. ( StoryFragment 함수의 주석처리된 부분을 제거하게 되면, 앱 진행 화면에서 기존에 막아두었던 기능인 챗봇 기능을 활성화 시킬 수 있습니다. )
5. 진행이 어렵다면 진행을 쉽게, 진행이 쉽다면 진행을 어렵게하는 변칙적인 변수와 게임에서 직접 앱 화면을 컨트롤하여 진행중인 사안의 노선을 틀어버리는 변수를 추가하였습니다.

## 실행 기능
- api 키를 입력할 수 있는 settings 버튼이 있습니다. 해당 버튼을 누르게 되면 클립보드에 복사된 내용을 붙여넣기 할 수 있거나 직접 입력할 수 있는 api 키 입력 공간이 있습니다. 그리고 체크하기 버튼을 누르게 되면 해당 api 키가 정상 작동하는지 확인하고 다음 진행부터 해당 api 키로 교체하여 진행하게 됩니다.
- 앱을 맨 처음 실행하는 경우 AI 에 대한 이미지가 바탕으로 생성이 되고, 기존에 진행중인 스크립트가 있는 경우 기존에 진행중인 내용을 토대로 배경 이미지가 생성됩니다.
- 주제 또한 원하는 주제로 입력할 수 있습니다. 아주 간략하게 입력을 하여도 그거를 토대로 chat gpt 에게 주어진 프롬포트를 이용하여 내용을 조금 더 다채롭게 만듭니다. 자세하게 입력할 경우에는 자세하게 입력된 부분을 조금 더 살리고 chat gpt가 참여하는 부분이 줄어든 상태로 내용을 생성하고 메인 화면과 마찬가지로 그 내용에 맞는 이미지를 생성합니다. 원하지 않는 내용이 나올경우 그 내용을 다시 생성할 수 있으며, 그대로 진행할 수도 있습니다.
- 그렇게 넘어온 화면에서는 3개의 버튼과 메인 화면이 있습니다. 먼저 메인 화면에서는 사용자가 선택을 진행하였을 때 나온 결과가 상단에 타이핑 사운드와 함께 타이핑되고, 그 아래에는 선택으로 발생한 바로 다음의 이야기가 아닌 일반 이야기가 출력되고, 다시 그 아래로 장비를 착용하였을 때의 문구와 장신구를 착용하였을 때의 문구가 나타납니다.
- 우측 상단에 정보 버튼을 클릭하게 되면 다양한 정보들이 나타나게 됩니다. 
1. 턴 : 어느 정도의 턴이 진행되고 있는지 나타납니다. 이를 통해 플레이어는 경과시간을 인식하게끔 합니다.
2. 난이도 : 현재 진행중인 난이도가 몇인지 확인할 수 있습니다. 기본적으로 난이도는 5 보통으로 가나 진행중인 상황에 따라 난이도가 변경되면 알려줍니다.
3. 시간 : 하루 24시간 중에서 어느 시간대에 있는지 나타냅니다. 낮과 밤에 따라 달라질 선택지를 효과적으로 작성할 수 있습니다.
4. 날짜 : 게임 시간대로 어느 정도의 날이 지났는지 나타냅니다. 턴과 다른 의미로 인식할 수 있습니다.
5. 날씨 : 선택에 가장 중요하다고 볼 수 있는 요소입니다. 예를 들어, 날씨가 흐린 날에 태양열 발전기를 키는 상황같은 것을 피할 수 있습니다.
6. 레벨 : 레벨이란 요소는 다르게 말하면 숙련도입니다. 어느 캐릭터가 좀 더 발전하고 있다는 것을 보여줍니다.
7. HP : 현재 건강상태를 표시하고 있습니다. 
8. 속성 : 주위를 이끌 수 있는 리더십, 직접 실행에 옮기도록 도와주는 힘, 상황을 해결해나갈 수 있는 지능, 무언가를 만들 수 있는 손재주, 어떤 일이든 해낼 수 있을 것 같다는 행운. 각 속성은 20이 최대이며 진행중인 스토리에 따라 자동으로 늘어나거나 줄어들 수 있습니다. 힘이 매우 작은 경우 지속적으로 HP가 줄어드는 등의 효과가 있습니다.
- StoryFragment의 주석을 해제하고, 우측 하단을 클릭하게 되면 대화창이 뜨게 됩니다. + 버튼을 눌러서 새로운 인물과 대화할 수 있고 이 + 버튼을 누를 수 있는 기회는 랜덤하게 주어집니다. 인물 프로필 사진을 클릭하게 되면 해당 인물의 이름, 성별, 나이, 성격, 사는 곳, 취미, 역할 같은 요소를 보여주며 대화의 진행에 도움을 줄 수 있습니다. 채팅방을 입력하게 되면 해당 인물과 대화할 수 있으며 각종 정보를 습득할 수 있습니다. 
- 좌측 하단을 클릭하게 되면 인벤토리 창이 뜨게 됩니다. 기본적으로 골드와 아이템이 있으며 각각을 소비하는 것을 한턴으로 분류하였습니다. 골드는 소지하고 있는 골드의 최대치를 넘을 수 없으며, 골드 같은 경우 어떻게 사용할지 입력할 수 있습니다. 아이템 같은 경우 한 번에 여러 아이템을 선택하여 사용할 수 있고 아이템의 개수에 따라 새로 창이 띄워지면서 사용을 결정합니다.
- 좌측으로 화면을 넘기게 되면 이미지 생성 화면이 나옵니다. 기본적으로 스토리 화면에서 스토리가 생성이 되면 자동 이미지가 생성되나 오류가 생기거나 새로운 이미지 생성을 원할 경우 버튼을 클릭하여 이미지를 생성할 수 있도록 합니다. 현재 진행중인 스토리에 맞춰서만 이미지가 생성됩니다.
- 우측으로 화면을 넘기게 되면 선택지 버튼이 있습니다. 1,2 번째 버튼은 기본적으로 chat gpt가 주어지는 선택지를 입력할 수 있는 버튼입니다. 우측 상단의 아이콘을 클릭하게 되면 chat gpt가 추천하는 선택지를 제공해줍니다. 없다면 사용할 수 없게 나타나있고 마지막으로 3번째 버튼은 사용자가 직접 입력할 수 있습니다. 영어로 입력하든 한국어로 입력하든 입력된 내용대로 다음 내용을 진행합니다.
- 뒤로가기 버튼을 두 번 누르게 되면, 현재 진행중인 내용을 저장할 수도 저장하지 않을 수도 있습니다.
- 저장된 내용을 가지고 다시 앱에 들어가게 되면 기존의 이야기를 이어나갈 수도 있고 새롭게 진행할 수도 있습니다.

## 빌드 방법
"apk 파일 공유" 에 들어가게 되면, apk 파일을 다운로드 할 수 있는 링크가 있습니다. 
모바일에서 진행을 할 수 있는 앱 형식입니다. 모바일에서 "파일"에 들어가 다운로드 파일을 보면 해당 apk 파일이 있습니다.
설정 → 애플리 케이션 → 연결 메뉴 → 특별한 접근 → 출처를 알 수 없는 앱에서 허용한 애플리케이션을 통해서 바로 apk 설치를 진행할 수 있습니다.

## 개발 빌드 방법
- apk 파일을 제외한 Myapplication2 파일을 보게 되면, gradle.properties 파일이 있습니다.
Version SDK = 33
Android Gradle Plugin Version = 7.4.0
Gradle Version 7.5

- python의 설치 경로를 다음과 같은 곳에서 지정할 수 있습니다.
build.gradle(Module :app) 의 buildpython에 Python 3.8 이 설치된 경로를 입력하시면 됩니다.


## 실행 화면
[ppt 파일로 업로드하였습니다.](https://docs.google.com/presentation/d/1eWM-0duuiCpjtNNpWgbGHTBPmDYZiRXx29EWBjFabCU/edit#slide=id.g226e19cf707_2_99)






