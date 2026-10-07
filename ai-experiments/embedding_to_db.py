from google import genai
import psycopg
import os


GEMINI_API_KEY = os.environ["GEMINI_API_KEY"]
DB_PASSWORD = os.environ["DB_PASSWORD"]


gemini_client = genai.Client(
    api_key=GEMINI_API_KEY
)


def get_embedding(text):
    response = gemini_client.models.embed_content(
        model="gemini-embedding-001",
        contents=text
    )

    return response.embeddings[0].values


documents = [
    {
        "content": "Drip irrigation helps conserve water in pomegranate farming.",
        "crop": "pomegranate",
        "topic": "irrigation",
        "source": "Day 9 test"
    },
    {
        "content": "Pomegranate plants require appropriate irrigation management during different growth stages.",
        "crop": "pomegranate",
        "topic": "irrigation",
        "source": "Day 9 test"
    },
    {
        "content": "Soil moisture should be monitored regularly when growing pomegranate.",
        "crop": "pomegranate",
        "topic": "soil",
        "source": "Day 9 test"
    },
    {
        "content": "Common pomegranate pests include aphids and fruit flies.",
        "crop": "pomegranate",
        "topic": "pests",
        "source": "Day 9 test"
    },
    {
        "content": "Wheat requires nitrogen fertilizer for healthy growth.",
        "crop": "wheat",
        "topic": "fertilizer",
        "source": "Day 9 test"
    }
]


connection_string = (
    f"dbname=ai_farming_assistant "
    f"user=sachin_ai "
    f"password={DB_PASSWORD} "
    f"host=localhost "
    f"port=5432"
)


with psycopg.connect(connection_string) as conn:

    with conn.cursor() as cursor:

        for document in documents:

            embedding = get_embedding(
                document["content"]
            )

            cursor.execute(
                """
                INSERT INTO farming_documents
                    (content, embedding, crop, topic, source)
                VALUES
                    (%s, %s, %s, %s, %s)
                """,
                (
                    document["content"],
                    embedding,
                    document["crop"],
                    document["topic"],
                    document["source"]
                )
            )

    conn.commit()


print("Documents and embeddings inserted successfully.")