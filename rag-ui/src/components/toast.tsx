import { useEffect, type FC } from "react";

export type ToastProps = {
  message: string|null;
  clearMessage: () => void;
};

const Toast: FC<ToastProps> = ({ message, clearMessage }) => {
    useEffect(() => {
      if (message && clearMessage) {
        console.log('setting timeout')
        setTimeout(clearMessage, 2000);
      }
    }, [message, clearMessage]);

    return message && (
      <div id="toast-default" className="flex items-center w-full mb-3 p-4 text-gray-500 bg-white rounded-lg shadow-sm dark:text-gray-400 dark:bg-gray-800" role="alert">
        <div className="ms-3 text-sm font-normal">{message}</div>
        <button onClick={clearMessage} type="button" className="ms-auto -mx-1.5 -my-1.5 bg-white text-gray-400 hover:text-gray-900 rounded-lg focus:ring-2 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center justify-center h-8 w-8 dark:text-gray-500 dark:hover:text-white dark:bg-gray-800 dark:hover:bg-gray-700">
          <span>X</span>
        </button>
      </div>
    );
}

export default Toast;