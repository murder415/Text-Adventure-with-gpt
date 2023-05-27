import openai
import time
import re
# OpenAI API 키 설정
openai.api_key = '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'

def call_openai_api(model, msg):
   model = "gpt-3.5-turbo"

   try:
       response = openai.ChatCompletion.create(
           model=model,
           messages=msg
       )
       return response
   except openai.error.APIError as e:
       print("API 호출 오류:", e)
       print("다시 시도합니다...")
       time.sleep(2)
       return call_openai_api(model, msg)
   except openai.error.RateLimitError as e:
        print("API 호출 오류:", e)
        print("다시 시도합니다...")
        time.sleep(20)  # 20초 대기
        return call_openai_api(model, msg)

def en2ko(input):
    input_pmt = f"{input}를 한국어로 번역해주세요"
    model = "gpt-3.5-turbo"


    print("1")
    print(input_pmt)

    while True:
        try:
            messages = [
               {
                   "role": "system",
                   "content": "You are a helpful assistant who is good at translating."
               },
               {
                   "role": "user",
                   "content": input_pmt
               }
            ]
            response = call_openai_api(model, messages)

        
            result = response['choices'][0]['message']['content']

            korean_pattern = re.compile("[\uac00-\ud7a3]+")
            print(result)
            
            # 결과가 한국어인지 확인
            if result:
                if korean_pattern.search(result):
                    return result
                else:
                    print("Empty translation. Retrying...")
                    time.sleep(1)
                    continue
        
        # 한도 에러가 발생하면 잠시 대기한 후 다시 시도
        except openai.error.RateLimitError:
            print("Rate limit exceeded. Waiting for 20 seconds...")
            time.sleep(20)
            continue
        
        # 다른 오류가 발생하면 잠시 대기한 후 다시 시도
        except Exception as e:
            print(f"Error: {e}. Retrying...")
            time.sleep(1)
            continue

def ko2en(input):
    input_pmt = f"{input}를 영어로 번역해주세요"
    model = "gpt-3.5-turbo"


    print("1")
    print(input_pmt)

    while True:
        try:
            messages = [
               {
                   "role": "system",
                   "content": "You are a helpful assistant who is good at translating."
               },
               {
                   "role": "user",
                   "content": input_pmt
               }
            ]
            response = call_openai_api(model, messages)
            result = response['choices'][0]['message']['content']

            english_pattern = re.compile("[A-Za-z]+")
            print(result)

            # 결과가 영어인지 확인
            if result:
                if english_pattern.search(result):
                    
                    return result
                
                else:
                    print("Empty translation. Retrying...")
                    time.sleep(1)
                    continue
        
        # 한도 에러가 발생하면 잠시 대기한 후 다시 시도
        except openai.error.RateLimitError:
            print("Rate limit exceeded. Waiting for 20 seconds...")
            time.sleep(20)
            continue
        
        # 다른 오류가 발생하면 잠시 대기한 후 다시 시도
        except Exception as e:
            print(f"Error: {e}. Retrying...")
            time.sleep(1)
            continue


