import { AssistantRuntimeProvider, useLocalRuntime, type ChatModelAdapter, type TextContentPart } from '@assistant-ui/react';
import './App.css'
import { Thread } from './components/assistant-ui/thread';
import AdminModal from './components/admin-modal';
import { useState } from 'react';
import Toast from './components/toast';
import { sleep } from "@/lib/utils";
import { v4 as uuidv4 } from 'uuid';

const DemoChatAdapter: ChatModelAdapter = {
  async run({ messages }) {
    // Fetch any conversation id or create a new one
    let conversationId = localStorage.getItem("conversation_id");
    if (conversationId == null) {
      conversationId = uuidv4();
      localStorage.setItem("conversation_id", conversationId);
    }

    let response = '';

    const userMessage = messages[messages.length-1].content.map((entry) => (entry as TextContentPart).text).join('');

    if (userMessage.startsWith("/help")) {
      response = 'Local commands are: \n';
      response += '  * __/help__ - List of commands.\n';
      response += '  * __/forgetme__ - Forget any past conversation.';
    } else if (userMessage.startsWith("/forgetme")) {
      localStorage.removeItem("conversation_id");
      response = 'Past conversation has been removed.';
    } else {
      // Send the chat request, the response is a token needed to get the results
      const startResult = await fetch('http://localhost:8080/chat', {
        method: "PATCH",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
          userMessage: userMessage,
          conversationId: conversationId
        })
      });
      
      const chatToken = await startResult.text();

      // Give the chat request up to 30 seconds to return an answer
      for (let i=0; i<30; i++) {
        await sleep(1000);

        const chatResult = await fetch(`http://localhost:8080/chat/${chatToken}`, {
          method: "GET",
          headers: {"Content-Type": "application/json"}
        });
        
        response = await chatResult.text();
        if (response != '') {
          break;
        }
      }

      if (response == '') {
        response = 'Timed out waiting for a response.'
      }
    }

    return {
      content: [{
          type: "text",
          text: response,
        },
      ],
    };
  },
};

function App() {
  const runtime = useLocalRuntime(DemoChatAdapter);
  const [showAdminModal, setShowAdminModal] = useState(false);
  const [toastMessage, setToastMessage] = useState<string|null>(null);

  return (
    <AssistantRuntimeProvider runtime={runtime}>
      <Toast message={toastMessage} clearMessage={() => setToastMessage(null)} />
        
      <button type="button" className="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-xs ring-1 ring-gray-300 ring-inset hover:bg-gray-50 sm:mt-0 sm:w-auto" onClick={() => setShowAdminModal(true)}>Add Content</button>
      
      <div className='mx-auto mt-6 flex h-[650px] w-full max-w-screen-xl flex-col overflow-hidden rounded-lg border shadow'>
        <AdminModal 
          show={showAdminModal} 
          onSuccess={(contentUrl) => setToastMessage(`Content added from ${contentUrl.slice(0, 60)}...`)} 
          onHide={() => setShowAdminModal(false)}
        />
        <Thread />
      </div>
    </AssistantRuntimeProvider>
  );
};

export default App
