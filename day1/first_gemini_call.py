import os
from google import genai
from google.genai import types

client = genai.Client(
    api_key=os.environ["GEMINI_API_KEY"]
)

response = client.models.generate_content(
    model="gemini-3.5-flash-lite",
    config=types.GenerateContentConfig(

        system_instruction="""

        You are an AI farming assistant.

        Explain farming concepts in simple and practical language.

        Do not invent information.

        If you do not have enough information to answer,

        clearly say that more information is required.

        """

    ),

    contents="""

    Explain tomato cultivation to an experienced farmer.

    """
)

print(response.text)