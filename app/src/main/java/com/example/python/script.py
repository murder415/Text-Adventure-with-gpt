import openai
from PyKakao import Karlo
from PIL import Image



def main(worldview):
     # 발급받은 API 키 설정
     OPENAI_API_KEY = "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"
     KAKAO_API_KEY = "9e1f646d2210318f877f3459be8d1efd"

     # openai API 키 인증
     openai.api_key = OPENAI_API_KEY

     # Karlo API 인스턴스 생성
     karlo = Karlo(service_key = KAKAO_API_KEY)

     # 모델 - GPT 3.5 Turbo 선택
     model = "gpt-3.5-turbo"

     # 질문 작성하기
     query = worldview

     # 메시지 설정하기
     messages = [
         {
             "role": "system",
             "content": "You are a helpful assistant who is good at detailing."
         },
         {
             "role": "user",
             "content": query
         }
     ]

     # ChatGPT API 호출하기
     response = openai.ChatCompletion.create(
         model=model,
         messages=messages
     )
     answer = response['choices'][0]['message']['content']
     print(answer)

     messages.append(
         {
             "role": "assistant",
             "content": answer
         },
     )

     # 사용자 메시지 추가
     messages.append(
         {
             "role": "user",
             "content": "위 내용을 바탕으로 모습을 더 자세히 상상해서 묘사해주세요."
         }
     )

     # ChatGPT API 호출하기
     response = openai.ChatCompletion.create(
         model=model,
         messages=messages
     )
     answer2 = response['choices'][0]['message']['content']
     print(answer2)

     # 새 메시지 구성
     messages = [
         {
             "role": "system",
             "content": "You are a helpful assistant who is good at translating."
         },
         {
             "role": "assistant",
             "content": answer2
         }
     ]

     # 사용자 메시지 추가
     messages.append(
         {
             "role": "user",
             "content": "영어로 번역해주세요."
         }
     )

     # ChatGPT API 호출하기
     response = openai.ChatCompletion.create(
         model=model,
         messages=messages
     )
     answer3 = response['choices'][0]['message']['content']
     print(answer3)


     # 새 메시지 구성
     messages = [
         {
             "role": "system",
             "content": "You are an assistant who is good at creating prompts for image creation."
         },
         {
             "role": "assistant",
             "content": answer3
         }
     ]

     # 사용자 메시지 추가
     messages.append(
         {
             "role": "user",
             "content": "Condense up to 4 outward description to focus on nouns and adjectives separated by ,"
         }
     )

     # ChatGPT API 호출하기
     response = openai.ChatCompletion.create(
         model=model,
         messages=messages
     )
     answer4 = response['choices'][0]['message']['content']
     print(answer4)

     # 이미지 생성을 위한 프롬프트
     params = ", concept art, realistic lighting, ultra-detailed, 8K, photorealism, digital art"
     prompt = f"Futuristic mars villages, {answer4}{params}"
     print(prompt)

     response = openai.Image.create(
       prompt=prompt,
       n=1,
       size="1024x1024"
     )
     image_url = response['data'][0]['url']
     return image_url

     # 이미지 생성하기 REST API 호출
     #img_dict = karlo.text_to_image(prompt, 1)

     # 생성된 이미지 정보
     #img_str = img_dict.get("images")[0].get('image')

     # base64 string을 이미지로 변환
     #img = karlo.string_to_image(base64_string = img_str, mode = 'RGBA')

     #img_bytes = img.tobytes()
     #return img_bytes


