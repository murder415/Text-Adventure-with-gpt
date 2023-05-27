import openai
import time

# OpenAI API 키 설정
openai.api_key = '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'

def get_response(input_text):

   print("123456789")
   print(input_text)

   input_pmt = f"{input_text}를 영어로 번역해주세요"

   print("1")
   print(input_pmt)

   while True:
       try:
           topic = openai.Completion.create(
               engine="text-davinci-003",
               prompt=input_pmt,
               max_tokens=1000,
               n=1,
               stop=None,
               temperature=0.7,
           )
           translate_pmt = topic.choices[0].text.strip()
           break
       except openai.error.RateLimitError as e:
           print("Rate limit reached. Waiting for 20 seconds...")
           time.sleep(20)


   print(topic)

   time.sleep(20)


   # 원래 내용
   original_prompt = """Please perform the creation theme function of a text adventure game, following the rules listed below:

presentation rules

1. The game output will always show 'title', 'theme' ,'world view' and 'story summary'

2. Wrap all game output in code blocks.

3. The subject is {translate_pmt}, please write the subject in a little more detail you want so that it can be used as a text adventure game.

4. The world view of the topic must be written within 2 lines"""

   original_prompt = original_prompt.replace('{translate_pmt}', translate_pmt)

   print("2")
   print(original_prompt)
   # OpenAI API 호출
   while True:
       try:
           response = openai.Completion.create(
               engine="text-davinci-003",
               prompt=original_prompt,
               max_tokens=1000,
               n=1,
               stop=None,
               temperature=0.7,
           )
           return_response = response.choices[0].text.strip()
           break
       except openai.error.RateLimitError as e:
           print("Rate limit reached. Waiting for 20 seconds...")
           time.sleep(20)


   print("3")
   return return_response


