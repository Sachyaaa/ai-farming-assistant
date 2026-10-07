from google import genai
import os
import math

client = genai.Client(
    api_key=os.environ["GEMINI_API_KEY"]
)


documents = [
    "Drip irrigation helps conserve water in pomegranate farming.",
    "Pomegranate plants require appropriate irrigation management during different growth stages.",
    "Wheat requires nitrogen fertilizer for healthy growth.",
    "Common pomegranate pests include aphids and fruit flies.",
    "Soil moisture should be monitored regularly when growing pomegranate."
]


def get_embedding(text):
    response = client.models.embed_content(
        model="gemini-embedding-001",
        contents=text
    )

    return response.embeddings[0].values


def cosine_similarity(vector_a, vector_b):
    dot_product = sum(
        a * b for a, b in zip(vector_a, vector_b)
    )

    magnitude_a = math.sqrt(
        sum(a * a for a in vector_a)
    )

    magnitude_b = math.sqrt(
        sum(b * b for b in vector_b)
    )

    return dot_product / (magnitude_a * magnitude_b)


# Create embeddings for documents
document_embeddings = [
    get_embedding(document)
    for document in documents
]


# User's question
query = "How can I improve water management for my pomegranate farm?"

query_embedding = get_embedding(query)


# Calculate similarity
results = []

for document, embedding in zip(
    documents,
    document_embeddings
):
    similarity = cosine_similarity(
        query_embedding,
        embedding
    )

    results.append((document, similarity))


# Sort highest similarity first
results.sort(
    key=lambda x: x[1],
    reverse=True
)


print("\nSemantic Search Results:\n")

for document, similarity in results:
    print(f"{similarity:.4f} → {document}")