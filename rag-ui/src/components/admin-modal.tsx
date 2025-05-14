import { sleep } from "@/lib/utils";
import { type FC, useCallback, useState } from "react";

export type AdminModalProps = {
  show: boolean;
  onSuccess: (contentUrl:string) => void;
  onHide: () => void;
};

const AdminModal: FC<AdminModalProps> = ({ show, onSuccess, onHide }) => {
  const [contentUrl, setContentUrl] = useState<string | undefined>();
  const [status, setStatus] = useState<string | undefined>();

  const saveUrl = useCallback(async () => {
    setStatus("CREATED");

    const startResult = await fetch('http://localhost:8080/admin/add-resource', {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: contentUrl
    });

    const requestId = await startResult.text();

    // Try for 20 seconds and then terminate
    for (let i=0; i<20; i++) {
      const processingResult = await fetch(`http://localhost:8080/admin/resource/${requestId}/status`, {
        method: "GET",
        headers: {"Content-Type": "application/json"}
      });
      
      const processingStatus = await processingResult.text();
      setStatus(processingStatus);

      if (processingStatus == 'DONE') {
        await sleep(1000);
        if (contentUrl) {
            onSuccess(contentUrl);
        }
        setStatus(undefined);
        onHide();
        break;
      }

      if (processingStatus == 'FAILED') {
        break;
      }

      if (i == 19) {
        setStatus('TIMEOUT');
      }

      await sleep(1000);
    }

  }, [contentUrl]);

  return show && (
    <div className="relative z-10" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div className="fixed inset-0 bg-gray-500/75 transition-opacity" aria-hidden="true"></div>
      <div className="fixed inset-0 z-10 w-screen overflow-y-auto">
        <div className="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
          <div className="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg">
            <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
              <h3 className="text-base font-semibold text-gray-900" id="modal-title">Add Content</h3>
              <div className="mt-2 w-full">
                <label className="block text-sm/6 font-medium text-gray-900">Content URL</label>
                <div className="flex items-center rounded-md bg-white pl-3 outline-1 -outline-offset-1 outline-gray-300 has-[input:focus-within]:outline-2 has-[input:focus-within]:-outline-offset-2 has-[input:focus-within]:outline-indigo-600">
                  <input type="text" name="price" id="price" className="block min-w-0 grow py-1.5 pr-3 pl-1 text-base text-gray-900 placeholder:text-gray-400 focus:outline-none sm:text-sm/6" placeholder="https://" onChange={(e) => setContentUrl(e.target.value)} />
                </div>
                {status && <div className="mt-2">{status}</div>}
              </div>
            </div>
            <div className="bg-gray-50 px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
              <button type="button" className="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-xs ring-1 ring-gray-300 ring-inset hover:bg-gray-50 sm:mt-0 sm:w-auto" onClick={saveUrl}>Save</button>
              <button type="button" className="mt-3 inline-flex w-full justify-center rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-xs ring-1 ring-gray-300 ring-inset hover:bg-gray-50 sm:mt-0 sm:w-auto" onClick={() => {setStatus(undefined); onHide();}}>Cancel</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminModal;