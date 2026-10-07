from pathlib import Path
import re

from dataclasses import dataclass


@dataclass
class DocumentChunk:
    content: str
    source: str
    chunk_index: int


def extract_text_from_txt(file_path):
    path = Path(file_path)

    return path.read_text(encoding="utf-8")


def clean_text(text):
    # Normalize line endings
    text = text.replace("\r\n", "\n")

    # Remove excessive whitespace
    text = re.sub(r"[ \t]+", " ", text)

    # Reduce excessive blank lines
    text = re.sub(r"\n{3,}", "\n\n", text)

    return text.strip()

def chunk_text(text, source, chunk_size=500, overlap=100):
    chunks = []

    start = 0
    chunk_index = 0

    while start < len(text):
        end = start + chunk_size

        chunk = text[start:end].strip()

        if chunk:
            chunks.append(
                DocumentChunk(
                    content=chunk,
                    source=source,
                    chunk_index=chunk_index
                )
            )

        start += chunk_size - overlap
        chunk_index += 1

    return chunks


text = extract_text_from_txt(
    "documents/pomegranate_guide.txt"
)

cleaned_text = clean_text(text)

chunks = chunk_text(
    cleaned_text,
    source="pomegranate_guide.txt",
    chunk_size=500,
    overlap=100
)

for chunk in chunks:
    print("\n--------------------")
    print("Index:", chunk.chunk_index)
    print("Source:", chunk.source)
    print("Content:")
    print(chunk.content)