from google import genai
import psycopg
import os


gemini_client = genai.Client(
    api_key=os.environ["GEMINI_API_KEY"]
)

DB_PASSWORD = os.environ["DB_PASSWORD"]


query = "How can I improve water management for my pomegranate farm?"


def get_embedding(text):
    response = gemini_client.models.embed_content(
        model="gemini-embedding-001",
        contents=text
    )

    return response.embeddings[0].values


query_embedding = get_embedding(query)


connection_string = (
    f"dbname=ai_farming_assistant "
    f"user=sachin_ai "
    f"password={DB_PASSWORD} "
    f"host=localhost "
    f"port=5432"
)


with psycopg.connect(connection_string) as conn:

    with conn.cursor() as cursor:

        cursor.execute(
            """
            SELECT
                id,
                content,
                crop,
                topic,
                source,
                embedding <=> %s::vector AS distance
            FROM farming_documents
            ORDER BY embedding <=> %s::vector
            LIMIT 3
            """,
            (
                query_embedding,
                query_embedding
            )
        )

        results = cursor.fetchall()


print("\nSemantic Search Results:\n")

for row in results:
    document_id, content, crop, topic, source, distance = row

    similarity = 1 - distance

    print(f"Similarity: {similarity:.4f}")
    print(f"Crop: {crop}")
    print(f"Topic: {topic}")
    print(f"Content: {content}")
    print("-" * 60)