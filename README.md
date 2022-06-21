# README

# 다이어리 키우기(DaKi)

## 사용기술

![Untitled](asset/Untitled 10.PNG)

# **나만의 일기장을 꾸밀 수 있는 서비스 DAKI**

## 1**. 기획 배경**

- 일기 작성에 어려움을 느끼는 사람들을 위한 서비스를 개발하고 싶다.
    - 일기 쓸 시간이 없는 바쁜 현대인을 위해
    - 일기를 예쁘게 꾸미고 싶지만 손재주가 부족한 사람을 위해

## 2**. 주요 서비스**

- 일기 작성
    - 일기를 작성하고 스티커나 이미지를 활용하여 꾸밀 수 있다.
    - 음성 인식을 이용하여 일기를 작성할 수 있다.
    - 달력으로 일기 작성한 날짜를 볼 수 있다.
    - 모바일 최적화를 통해 모바일로도 일기 작성이 가능하다.
- 캐릭터 꾸미기
    - 일기 작성을 통해 얻은 포인트로 캐릭터를 꾸미는 아이템을 얻을 수 있다.
    - 아이템을 입혀 캐릭터를 꾸밀 수 있다.
    

## 3**. 아키텍처**

![image](https://user-images.githubusercontent.com/30211245/174721550-0c80b50a-9435-4209-b3b4-0a290a58fb7b.png)


## 4**. 서비스 화면**

### **메인페이지**

![image](https://user-images.githubusercontent.com/30211245/174721596-2ad6573d-e4bd-44c2-89a2-4a7bac51cb58.png)


### 로그인

![image](https://user-images.githubusercontent.com/30211245/174721626-e3903fa4-1e50-45e1-9d34-bdc471a964a7.png)


### 회원가입
![image](https://user-images.githubusercontent.com/30211245/174721655-e075dd97-1155-447e-bc74-3513310cf239.png)
![image](https://user-images.githubusercontent.com/30211245/174721672-372cf2a6-9764-44df-a24f-dc2bbe29771e.png)


### 일기 캘린더
![image](https://user-images.githubusercontent.com/30211245/174721686-af366a0d-c708-4f1f-a364-4c01ac11c2e6.png)
### 일기 그리드로 보기

![image](https://user-images.githubusercontent.com/30211245/174721750-83ebfb32-1cbd-4ee3-a08c-1f1aa0facedb.png)

### 일기 작성 페이지

![image](https://user-images.githubusercontent.com/30211245/174721700-b9dbd26e-8232-4634-8bd8-9e7bec1fd10b.png)

### 마이페이지
![image](https://user-images.githubusercontent.com/30211245/174721766-669ea34c-9858-4d9b-8b5b-7e0705df048c.png)

### 캐릭터 꾸미기 탭

![image](https://user-images.githubusercontent.com/30211245/174721776-17ca3d73-7647-411b-8fb0-5d80956022be.png)

## 5**. UCC**

## 6**. 기술 스택**

| 구분 | S/W | version |
| --- | --- | --- |
| Server | ubuntu | Ubuntu 20.04.3 LTS |
|  | Docker | 20.10.14 |
|  |  |  |
| AWS | EC2 |  |
|  | S3 |  |
|  | Route 53 |  |
|  |  |  |
| Database | MySQL | 5.7.37 |
|  |  |  |
| Backend | Java | 1.8 |
|  | Spring boot | 2.6.4 |
|  |  |  |
| Frontend | node.js | 16.13.1 |
|  | Vue.js | 2.6.14 |
|  |  |  |
| IDE | VS code | 1.66.0 |
|  | IntelliJ | 20213.1 |



## 7. 포팅메뉴얼

### 1. 개발환경

사용한 제품 종류 및 버전

| 구분     | S/W         | version            |
| -------- | ----------- | ------------------ |
| Server   | ubuntu      | Ubuntu 20.04.3 LTS |
|          | Docker      | 20.10.14           |
|          |             |                    |
| Database | MySQL       | 5.7.37             |
|          |             |                    |
| Backend  | Java        | 1.8                |
|          | Spring boot | 2.6.4              |
|          |             |                    |
| Frontend | node.js     | 16.13.1            |
|          | Vue.js      | 2.6.14             |
|          |             |                    |
| IDE      | VS code     | 1.66.0             |
|          | IntelliJ    | 20213.1            |



### 2. 프로젝트 빌드 전 설치요소(특이사항)

#### 2.1. 방화벽 설정

서비에서 사용할 포트를 미리 열어줍니다.

| 22/TCP   | SSH                    |
| -------- | ---------------------- |
| 443/TCP  | nginx                  |
| 3306/TCP | MySQL                  |
| 8080/TCP | Spring boot API Server |

#### 2.2. MySQL

```bash
# 설치
sudo apt-get update
sudo apt-get install mysql-server

# 실행
sudo service mysql start

# 접속
sudo mysql

# 사용할 유저 생성 및 권한 부여
CREATE USER 계정이름@'%' IDENTIFIED BY 비밀번호;
GRANT ALL PRIVILEGES ON *.* TO 계정이름@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES
```

#### 2.3. 인증서

```bash
# certbot 설치
sudo snap install certbot --classic

# 인증서 발급
sudo certbot certonly --standalone

# root 계정 로그인
sudo su

# 인증서 위치 폴더 이동
cd /etc/letsencrypt/live/k6e105.p.ssafy.io

# 인증서 형식 변경 pem -> PKCS12 
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out cert.p12 -CAfile chain.pem

# 인증서 복사
sudo cp fullchain.pem /home/ssl
sudo cp privkey.pem /home/ssl
sudo cp key.p12 /home/ssl
```

#### 2.4. Docker

```bash
# 도커 설치
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io
```



### 3. 배포 및 실행

AWS EC2 와 docker를 이용하여 배포하였습니다. 

#### 3.1. Nginx 환경 설정

```jsx
server {
	listen 443 ssl;                
	server_name k6e105.p.ssafy.io;
	
	ssl_certificate     /home/ssl/fullchain.pem;
	ssl_certificate_key /home/ssl/privkey.pem;
	
	
	location / {
	        root /home/frontend/dist;
	        #root   /usr/share/nginx/html;
	        index  index.html index.htm;
	    }
}
```

#### 3.2. 프로젝트 다운 (git clone)

```jsx
git clone https://lab.ssafy.com/s06-final/S06P31E105.git
```

#### 3.3. api 서버 빌드 및 실행

```jsx
mvn clean package
docker build -t daki:latest .
docker run --name daki -p 8080:8080-d -v /home/ssl:/home/ssl --rm daki:latest
```

#### 3.4. nginx 실행

```jsx
service nginx start
```

